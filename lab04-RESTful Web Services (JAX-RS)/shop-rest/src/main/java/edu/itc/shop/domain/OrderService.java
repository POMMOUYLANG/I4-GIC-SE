package edu.itc.shop.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jakarta.inject.Inject;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import edu.itc.shop.api.dto.PlaceOrder;

public class OrderService {

    private final ProductRepository productRepo;
    private final ConcurrentMap<String, Order> orders = new ConcurrentHashMap<>();

    @Inject
    public OrderService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    /** Validates every product and reserves inventory atomically before saving the new order. */
    public synchronized Order place(PlaceOrder cmd) {
        Map<Long, Product> loaded = new LinkedHashMap<>();
        Map<Long, Integer> requestedQty = new LinkedHashMap<>();

        for (PlaceOrder.LineItem item : cmd.lines()) {
            long pid = item.productId();
            loaded.computeIfAbsent(pid, id -> productRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Product " + id + " not found")));
            requestedQty.merge(pid, item.quantity(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : requestedQty.entrySet()) {
            Product p = loaded.get(entry.getKey());
            int qty = entry.getValue();
            if (p.stock() < qty) {
                throw new InsufficientStockException(p.id(), p.stock(), -qty);
            }
        }

        for (Map.Entry<Long, Integer> entry : requestedQty.entrySet()) {
            Product p = loaded.get(entry.getKey());
            productRepo.save(p.withStock(p.stock() - entry.getValue()));
        }

        BigDecimal total = BigDecimal.ZERO;
        List<Order.Line> lines = new ArrayList<>();
        for (PlaceOrder.LineItem item : cmd.lines()) {
            Product p = loaded.get(item.productId());
            total = total.add(p.price().multiply(BigDecimal.valueOf(item.quantity())));
            lines.add(new Order.Line(p.id(), item.quantity()));
        }

        String orderId = "o-" + UUID.randomUUID().toString().substring(0, 8);
        Order order = new Order(orderId, List.copyOf(lines), total, OrderStatus.NEW);
        orders.put(orderId, order);
        return order;
    }

    public Order get(String id) {
        Order o = orders.get(id);
        if (o == null) {
            throw new NotFoundException("Order " + id + " not found");
        }
        return o;
    }

    public synchronized Order pay(String id) {
        Order o = get(id);
        if (o.status() != OrderStatus.NEW) {
            throw new ClientErrorException("Cannot pay order in status " + o.status(), Response.Status.CONFLICT);
        }
        Order updated = new Order(o.id(), o.lines(), o.total(), OrderStatus.PAID);
        orders.put(id, updated);
        return updated;
    }

    /** Cancels a NEW order and restores reserved stock; idempotent for already-CANCELLED orders. */
    public synchronized Order cancel(String id) {
        Order o = get(id);
        if (o.status() == OrderStatus.PAID) {
            throw new ClientErrorException("Cannot cancel paid order", Response.Status.CONFLICT);
        }
        if (o.status() == OrderStatus.CANCELLED) {
            return o;
        }

        Map<Long, Integer> restoreQty = new LinkedHashMap<>();
        for (Order.Line line : o.lines()) {
            restoreQty.merge(line.productId(), line.quantity(), Integer::sum);
        }
        for (Map.Entry<Long, Integer> entry : restoreQty.entrySet()) {
            productRepo.findById(entry.getKey()).ifPresent(p ->
                    productRepo.save(p.withStock(p.stock() + entry.getValue())));
        }

        Order cancelled = new Order(o.id(), o.lines(), o.total(), OrderStatus.CANCELLED);
        orders.put(id, cancelled);
        return cancelled;
    }
}
