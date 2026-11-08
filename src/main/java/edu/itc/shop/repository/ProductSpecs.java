package edu.itc.shop.repository;

import edu.itc.shop.domain.Category;
import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecs {
    private ProductSpecs() {}

    public static Specification<Product> nameContains(String q) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("name")), "%" + q.toLowerCase() + "%");
    }

    public static Specification<Product> inCategory(Category category) {
        return (root, query, cb) ->
            cb.equal(root.get("category"), category);
    }

    public static Specification<Product> cheaperThan(BigDecimal max) {
        return (root, query, cb) ->
            cb.le(root.<Money>get("price").<BigDecimal>get("amount"), max);
    }

    public static Specification<Product> inStock() {
        return (root, query, cb) ->
            cb.gt(root.get("stock"), 0);
    }

    public static Specification<Product> filter(String q, Category category, BigDecimal maxPrice, Boolean inStock) {
        return Specification.allOf(
            q != null && !q.isBlank() ? nameContains(q) : null,
            category != null ? inCategory(category) : null,
            maxPrice != null ? cheaperThan(maxPrice) : null,
            Boolean.TRUE.equals(inStock) ? inStock() : null
        );
    }
}
