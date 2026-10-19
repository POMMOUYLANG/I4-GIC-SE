package edu.itc.se.shop.web.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ProductViewModel(long id, String name, BigDecimal price, BigDecimal gross, int stock) {
    public static ProductViewModel of(ProductDto dto, BigDecimal taxRate) {
        BigDecimal gross = dto.price().multiply(BigDecimal.ONE.add(taxRate)).setScale(2, RoundingMode.HALF_UP);
        return new ProductViewModel(dto.id(), dto.name(), dto.price(), gross, dto.stock());
    }
}
