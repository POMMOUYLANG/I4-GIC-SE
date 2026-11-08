package edu.itc.shop.web.dto;

import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.Order;
import edu.itc.shop.domain.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderDto(
    Long id,
    OrderStatus status,
    Instant placedAt,
    Money total,
    List<OrderLineDto> lines
) {
    public static OrderDto from(Order o) {
        return new OrderDto(
            o.getId(),
            o.getStatus(),
            o.getPlacedAt(),
            o.total(),
            o.getLines().stream()
                .map(OrderLineDto::from)
                .toList()
        );
    }

    public record OrderLineDto(
        Long id,
        String productSku,
        String productName,
        int quantity,
        Money unitPrice
    ) {
        public static OrderLineDto from(edu.itc.shop.domain.OrderLine l) {
            return new OrderLineDto(
                l.getId(),
                l.getProduct().getSku(),
                l.getProduct().getName(),
                l.getQuantity(),
                l.getUnitPrice()
            );
        }
    }
}
