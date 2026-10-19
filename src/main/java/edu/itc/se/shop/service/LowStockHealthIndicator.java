package edu.itc.se.shop.service;

import edu.itc.se.shop.config.ShopProperties;
import edu.itc.se.shop.domain.Product;
import edu.itc.se.shop.repository.ProductRepository;
import java.util.List;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom Actuator Health Indicator.
 * Marks application status as DOWN if any product stock is lower than threshold.
 */
@Component("lowStock")
public class LowStockHealthIndicator implements HealthIndicator {

    private final ProductRepository productRepository;
    private final ShopProperties properties;

    public LowStockHealthIndicator(ProductRepository productRepository, ShopProperties properties) {
        this.productRepository = productRepository;
        this.properties = properties;
    }

    @Override
    public Health health() {
        int threshold = properties.lowStockThreshold();
        List<String> lowStockProducts = productRepository.findAll().stream()
                .filter(p -> p.stock() < threshold)
                .map(Product::name)
                .toList();

        if (lowStockProducts.isEmpty()) {
            return Health.up().build();
        }

        return Health.down()
                .withDetail("lowStockProducts", lowStockProducts)
                .build();
    }
}
