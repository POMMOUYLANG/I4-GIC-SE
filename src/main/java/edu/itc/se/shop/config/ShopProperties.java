package edu.itc.se.shop.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Strongly typed and validated configuration properties under the prefix 'shop'.
 */
@Validated
@ConfigurationProperties(prefix = "shop")
public record ShopProperties(
    @NotBlank(message = "Currency code must be configured")
    String currency,

    @DecimalMin(value = "0.0", message = "Tax rate cannot be negative")
    @DecimalMax(value = "1.0", message = "Tax rate cannot exceed 100%")
    BigDecimal taxRate,

    @Min(value = 1, message = "Low stock threshold must be at least 1")
    int lowStockThreshold
) {}
