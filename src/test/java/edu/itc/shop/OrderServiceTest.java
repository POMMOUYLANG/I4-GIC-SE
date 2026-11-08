package edu.itc.shop;

import edu.itc.shop.domain.*;
import edu.itc.shop.repository.CustomerRepository;
import edu.itc.shop.repository.OrderRepository;
import edu.itc.shop.repository.ProductRepository;
import edu.itc.shop.service.OrderService;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManagerFactory emf;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = customerRepository.findByEmail("test_dirty@example.com").orElseGet(() ->
            customerRepository.save(new Customer(
                "Test Customer",
                "test_dirty@example.com",
                new Address("1 Street", "City", "10000"),
                new Address("1 Street", "City", "10000")
            ))
        );
    }

    @Test
    void testManagedEntityDirtyChecking() {
        Order order = new Order(customer);
        order = orderRepository.save(order);
        Long orderId = order.getId();

        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();
        long initialUpdates = stats.getEntityUpdateCount();

        // Calling markPaid inside a transaction modifies the managed entity without explicit save()
        orderService.markPaid(orderId);

        // Verify entity updated in DB via Hibernate dirty checking
        Order updated = orderRepository.findById(orderId).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(stats.getEntityUpdateCount()).isGreaterThan(initialUpdates);
    }

    @Test
    void testDetachedEntityModification() {
        Product product = productRepository.save(new Product(
            "SKU-D-" + (System.currentTimeMillis() % 1000000), "Detached Test", Money.eur("20.00"), Category.HARDWARE, 50
        ));
        Long prodId = product.getId();

        // 1. Fetch outside transaction (detached)
        Product detached = productRepository.findById(prodId).orElseThrow();
        detached.setStock(99);

        // 2. Without calling save(), DB remains unchanged
        Product reloaded = productRepository.findById(prodId).orElseThrow();
        assertThat(reloaded.getStock()).isEqualTo(50);

        // 3. Calling save() merges detached changes
        productRepository.save(detached);
        Product saved = productRepository.findById(prodId).orElseThrow();
        assertThat(saved.getStock()).isEqualTo(99);
    }

    @Test
    void testReadOnlyTrap() {
        Order order = new Order(customer);
        order = orderRepository.save(order);
        Long orderId = order.getId();

        // wrongReadOnlyModification runs with @Transactional(readOnly = true)
        // Hibernate FlushMode.MANUAL suppresses dirty checking snapshots
        orderService.wrongReadOnlyModification(orderId);

        Order reloaded = orderRepository.findById(orderId).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.NEW);
    }
}
