package edu.itc.se.shop.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Input DTO for creating a new product with Bean Validation constraints.
 */
public record CreateProduct(
    @NotBlank(message = "Product name is required")
    @Size(max = 80, message = "Product name must not exceed 80 characters")
    String name,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    BigDecimal price,

    @PositiveOrZero(message = "Stock must be zero or positive")
    int stock
) {}
