package edu.itc.se.shop.domain;

import java.math.BigDecimal;

/**
 * Domain entity represented as an immutable Java Record.
 */
public record Product(Long id, String name, BigDecimal price, int stock) {
    // Compact constructor to guarantee non-negative stock at creation time
    public Product {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative: " + stock);
        }
    }
}
