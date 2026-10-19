package edu.itc.se.shop.web.dto;

import edu.itc.se.shop.domain.Product;
import java.math.BigDecimal;

/**
 * Output DTO sent back to clients.
 */
public record ProductDto(long id, String name, BigDecimal price, int stock) {
    public static ProductDto from(Product p) {
        return new ProductDto(p.id(), p.name(), p.price(), p.stock());
    }
}
