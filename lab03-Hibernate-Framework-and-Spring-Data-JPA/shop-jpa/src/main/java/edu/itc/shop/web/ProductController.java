package edu.itc.shop.web;

import edu.itc.shop.domain.Category;
import edu.itc.shop.repository.ProductRepository;
import edu.itc.shop.repository.ProductSpecs;
import edu.itc.shop.web.dto.ProductView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public Page<ProductView> search(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Category category,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) Boolean inStock,
        Pageable pageable
    ) {
        return productRepository.findAll(ProductSpecs.filter(q, category, maxPrice, inStock), pageable)
            .map(p -> new ProductView(p.getId(), p.getSku(), p.getName(), p.getPrice().amount(), p.getStock()));
    }
}
