package edu.itc.shop;

import edu.itc.shop.domain.Address;
import edu.itc.shop.domain.Customer;
import edu.itc.shop.domain.Order;
import edu.itc.shop.domain.OrderStatus;
import edu.itc.shop.repository.CustomerRepository;
import edu.itc.shop.repository.OrderRepository;
import edu.itc.shop.service.OrderFacade;
import edu.itc.shop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OptimisticLockingTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = customerRepository.findByEmail("opt@example.com").orElseGet(() ->
            customerRepository.save(new Customer(
                "Opt Customer",
                "opt@example.com",
                new Address("Opt St", "City", "12345"),
                new Address("Opt St", "City", "12345")
            ))
        );
    }

    @Test
    void testVersionIncrementOnUpdate() {
        Order order = orderRepository.save(new Order(customer));
        assertThat(order.getVersion()).isEqualTo(0L);

        orderService.changeStatus(order.getId(), OrderStatus.PAID);
        Order updated1 = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updated1.getVersion()).isEqualTo(1L);

        orderService.changeStatus(order.getId(), OrderStatus.SHIPPED);
        Order updated2 = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updated2.getVersion()).isEqualTo(2L);
    }

    @Test
    void testOptimisticLockingCollision() throws Exception {
        Order order = orderRepository.save(new Order(customer));
        Long orderId = order.getId();

        CountDownLatch bothLoadedLatch = new CountDownLatch(2);
        CountDownLatch firstCommittedLatch = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        Callable<Void> task1 = () -> {
            try {
                transactionTemplate.execute(status -> {
                    Order o = orderRepository.findById(orderId).orElseThrow();
                    bothLoadedLatch.countDown();
                    try {
                        bothLoadedLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    o.setStatus(OrderStatus.PAID);
                    return null;
                });
                successCount.incrementAndGet();
            } catch (Exception ex) {
                failureCount.incrementAndGet();
            } finally {
                firstCommittedLatch.countDown();
            }
            return null;
        };

        Callable<Void> task2 = () -> {
            try {
                transactionTemplate.execute(status -> {
                    Order o = orderRepository.findById(orderId).orElseThrow();
                    bothLoadedLatch.countDown();
                    try {
                        bothLoadedLatch.await();
                        firstCommittedLatch.await(); // wait for task 1 to commit its new version
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    o.setStatus(OrderStatus.CANCELLED);
                    return null;
                });
                successCount.incrementAndGet();
            } catch (ObjectOptimisticLockingFailureException ex) {
                failureCount.incrementAndGet();
            } catch (Exception ex) {
                if (ex.getCause() instanceof ObjectOptimisticLockingFailureException) {
                    failureCount.incrementAndGet();
                } else {
                    throw ex;
                }
            }
            return null;
        };

        Future<Void> f1 = executor.submit(task1);
        Future<Void> f2 = executor.submit(task2);
        f1.get(5, TimeUnit.SECONDS);
        f2.get(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(1);
    }

    @Test
    void testOrderFacadeRecoversWithRetry() {
        Order order = orderRepository.save(new Order(customer));
        Long orderId = order.getId();

        // Ensure retry facade successfully updates status
        orderFacade.changeStatusWithRetry(orderId, OrderStatus.PAID);
        Order reloaded = orderRepository.findById(orderId).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(reloaded.getVersion()).isEqualTo(1L);
    }
}
