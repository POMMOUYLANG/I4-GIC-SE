package edu.itc.se.shop.service;

import edu.itc.se.shop.domain.Product;
import edu.itc.se.shop.repository.ProductRepository;
import edu.itc.se.shop.web.dto.CreateProduct;
import edu.itc.se.shop.web.dto.ProductDto;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<ProductDto> search(String query) {
        List<Product> products = (query == null || query.isBlank())
                ? productRepository.findAll()
                : productRepository.findByNameContaining(query);

        return products.stream().map(ProductDto::from).toList();
    }

    public ProductDto getById(long id) {
        return productRepository.findById(id)
                .map(ProductDto::from)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public ProductDto create(CreateProduct cmd) {
        Product toInsert = new Product(null, cmd.name(), cmd.price(), cmd.stock());
        long generatedId = productRepository.insert(toInsert);
        return new ProductDto(generatedId, cmd.name(), cmd.price(), cmd.stock());
    }

    @Transactional
    public ProductDto updateProductStock(long id, int delta) {
        Product current = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (current.stock() + delta < 0) {
            throw new InsufficientStockException(id, current.stock(), delta);
        }

        productRepository.updateStock(id, delta);
        return new ProductDto(current.id(), current.name(), current.price(), current.stock() + delta);
    }

    @Transactional
    public void delete(long id) {
        int affected = productRepository.delete(id);
        if (affected == 0) {
            throw new ProductNotFoundException(id);
        }
    }

    /**
     * Transfers 'qty' stock from product 'fromId' to product 'toId'.
     *
     * @Transactional guarantees ATOMICITY:
     * If the source product does not have enough stock, or if the second product
     * fails to update, an exception is thrown and the FIRST update is ROLLED BACK!
     */
    @Transactional
    public void restock(long fromId, long toId, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        log.info("Attempting transfer of {} units from Product #{} to Product #{}", qty, fromId, toId);

        // 1. Deduct from source product
        int rowsUpdated1 = productRepository.updateStock(fromId, -qty);
        if (rowsUpdated1 == 0) {
            throw new IllegalStateException("Insufficient stock or product not found for ID: " + fromId);
        }

        // 2. Add to destination product
        int rowsUpdated2 = productRepository.updateStock(toId, qty);
        if (rowsUpdated2 == 0) {
            throw new IllegalStateException("Destination product not found for ID: " + toId);
        }

        log.info("Restock successful! Transferred {} units from #{} to #{}", qty, fromId, toId);
    }
}
