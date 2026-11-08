package edu.itc.shop;

import edu.itc.shop.domain.*;
import edu.itc.shop.repository.CustomerRepository;
import edu.itc.shop.repository.OrderRepository;
import edu.itc.shop.repository.ProductRepository;
import edu.itc.shop.support.QueryCounter;
import jakarta.persistence.EntityManager;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class NPlusOneTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private QueryCounter queryCounter;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EntityManager em;

    private Customer customer;
    private Product p1;
    private Product p2;

    @BeforeEach
    void setUp() {
        transactionTemplate.execute(status -> {
            customer = customerRepository.findByEmail("nplusone@example.com").orElseGet(() ->
                customerRepository.save(new Customer("N1 Customer", "nplusone@example.com",
                    new Address("N1 St", "City", "00000"), new Address("N1 St", "City", "00000")))
            );

            p1 = productRepository.findBySku("SKU-N1-1").orElseGet(() ->
                productRepository.save(new Product("SKU-N1-1", "N1 Product 1", Money.eur("10.00"), Category.HARDWARE, 100))
            );
            p2 = productRepository.findBySku("SKU-N1-2").orElseGet(() ->
                productRepository.save(new Product("SKU-N1-2", "N1 Product 2", Money.eur("20.00"), Category.HARDWARE, 100))
            );

            // Seed 10 PAID orders with lines if not already present
            if (orderRepository.findByStatusWithLines(OrderStatus.PAID).size() < 10) {
                for (int i = 0; i < 10; i++) {
                    Order o = new Order(customer);
                    o.setStatus(OrderStatus.PAID);
                    o.addLine(p1, 1);
                    o.addLine(p2, 2);
                    orderRepository.save(o);
                }
            }
            return null;
        });
    }

    @Test
    @DisplayName("Strategy A: JPQL JOIN FETCH eliminates N+1 into exactly 1 statement")
    void testFetchJoinStrategyGeneratesSingleStatement() {
        transactionTemplate.execute(status -> {
            em.clear();
            queryCounter.reset();

            List<Order> orders = orderRepository.findByStatusWithLines(OrderStatus.PAID);
            assertThat(orders).isNotEmpty();

            // Iterate lines to verify eager initialization without additional SQL queries
            for (Order o : orders) {
                assertThat(o.getLines()).hasSize(2);
            }

            assertThat(queryCounter.count()).isEqualTo(1L);
            return null;
        });
    }

    @Test
    @DisplayName("Strategy B: Spring Data @EntityGraph eliminates N+1 into 1 statement")
    void testEntityGraphStrategyGeneratesSingleStatement() {
        transactionTemplate.execute(status -> {
            em.clear();
            queryCounter.reset();

            List<Order> orders = orderRepository.findWithGraphByStatus(OrderStatus.PAID);
            assertThat(orders).isNotEmpty();

            // Iterate nested lines and products
            for (Order o : orders) {
                assertThat(o.getLines()).hasSize(2);
                assertThat(o.getLines().get(0).getProduct().getName()).isEqualTo("N1 Product 1");
            }

            assertThat(queryCounter.count()).isEqualTo(1L);
            return null;
        });
    }

    @Test
    @DisplayName("Strategy C: @BatchSize(16) reduces N+1 statements into batched IN clauses")
    void testBatchSizeReducesStatementCount() {
        transactionTemplate.execute(status -> {
            em.clear();
            queryCounter.reset();

            List<Order> orders = orderRepository.findByStatus(OrderStatus.PAID);
            assertThat(orders).isNotEmpty();

            // Trigger lazy loading on lines for all orders
            for (Order o : orders) {
                assertThat(o.getLines()).hasSize(2);
            }

            // With @BatchSize(16) and 10 orders, statements = 1 (orders query) + 1 (batched lines query) = 2
            // Far below naive 1 + 10 = 11 statements
            assertThat(queryCounter.count()).isLessThanOrEqualTo(3L);
            return null;
        });
    }

    @Test
    @DisplayName("open-in-view: false enforces boundary and throws LazyInitializationException outside session")
    void testOpenInViewEnforcesBoundary() {
        // Fetch order inside transaction without initializing lazy lines
        Long orderId = transactionTemplate.execute(status -> {
            Order o = orderRepository.save(new Order(customer));
            o.addLine(p1, 1);
            return o.getId();
        });

        // Outside transaction proxy, Session is closed
        Order detachedOrder = transactionTemplate.execute(status -> {
            em.clear();
            return orderRepository.findById(orderId).orElseThrow();
        });

        // Accessing lazy collection outside transaction throws LazyInitializationException
        assertThatThrownBy(() -> detachedOrder.getLines().size())
            .isInstanceOf(LazyInitializationException.class);
    }
}
