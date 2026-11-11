package edu.itc.shop.domain;

import java.util.List;
import jakarta.ws.rs.NotFoundException;

public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> list() {
        return repo.findAll();
    }

    public Product get(long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }

    public synchronized Product create(Product draft) {
        repo.findBySku(draft.sku()).ifPresent(p -> {
            throw new DuplicateSkuException(draft.sku());
        });
        return repo.save(new Product(0, draft.name(), draft.sku(), draft.price(), draft.stock(), 0));
    }

    public synchronized Product update(long id, Product replacement) {
        Product existing = get(id);
        repo.findBySku(replacement.sku())
                .filter(other -> other.id() != id)
                .ifPresent(other -> {
                    throw new DuplicateSkuException(replacement.sku());
                });
        return repo.save(new Product(
                id,
                replacement.name(),
                replacement.sku(),
                replacement.price(),
                replacement.stock(),
                existing.version()
        ));
    }

    public void delete(long id) {
        if (!repo.delete(id)) {
            throw new NotFoundException("Product " + id + " not found");
        }
    }

    public synchronized Product adjustStock(long id, int delta) {
        Product existing = get(id);
        int updatedStock = existing.stock() + delta;
        if (updatedStock < 0) {
            throw new InsufficientStockException(id, existing.stock(), delta);
        }
        return repo.save(new Product(
                existing.id(),
                existing.name(),
                existing.sku(),
                existing.price(),
                updatedStock,
                existing.version()
        ));
    }
}
