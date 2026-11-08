package edu.itc.shop.service;

import edu.itc.shop.domain.OrderStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class OrderFacade {

    private final OrderService orderService;

    public OrderFacade(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Retries the transaction up to 3 times on optimistic lock collisions.
     * MUST live outside OrderService so that each attempt invokes the transaction proxy.
     */
    public void changeStatusWithRetry(Long orderId, OrderStatus next) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                orderService.changeStatus(orderId, next);
                return;
            } catch (ObjectOptimisticLockingFailureException ex) {
                if (attempt == maxAttempts) throw ex;
                try {
                    Thread.sleep(20L * attempt); // Back-off delay
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(ie);
                }
            }
        }
    }
}
