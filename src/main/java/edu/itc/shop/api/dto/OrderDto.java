package edu.itc.shop.api.dto;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.ws.rs.core.UriInfo;

import edu.itc.shop.domain.Order;
import edu.itc.shop.domain.OrderStatus;

public record OrderDto(
        String id,
        OrderStatus status,
        BigDecimal total,
        List<Order.Line> lines,
        Map<String, String> _links
) {
    public static OrderDto from(Order o, UriInfo uriInfo) {
        String base = uriInfo.getBaseUri().toString();
        String orderUri = base + "orders/" + o.id();

        Map<String, String> links = new LinkedHashMap<>();
        links.put("self", orderUri);

        if (o.status() == OrderStatus.NEW) {
            links.put("pay", orderUri + "/payment");
            links.put("cancel", orderUri);
        }

        for (Order.Line line : o.lines()) {
            links.putIfAbsent("product-" + line.productId(), base + "products/" + line.productId());
        }

        return new OrderDto(o.id(), o.status(), o.total(), o.lines(), Map.copyOf(links));
    }
}
