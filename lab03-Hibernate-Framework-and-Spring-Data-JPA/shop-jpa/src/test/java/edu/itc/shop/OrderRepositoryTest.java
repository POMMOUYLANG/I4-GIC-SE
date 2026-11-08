package edu.itc.shop;

import edu.itc.shop.domain.*;
import edu.itc.shop.repository.CustomerRepository;
import edu.itc.shop.repository.OrderRepository;
import edu.itc.shop.repository.ProductRepository;
import edu.itc.shop.web.dto.OrderSummary;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired private OrderRepository orderRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private TestEntityManager em;

    @Test
    @DisplayName("Fetch join initializes lines eagerly in 1 query, whereas findById keeps lines uninitialized")
    void testFetchJoinVsPlainFind() {
        Customer c = customerRepository.save(new Customer("Test", "test@test.com",
            new Address("S", "C", "Z"), new Address("S", "C", "Z")));
        Product p = productRepository.save(new Product("P-1", "Test Prod", Money.eur("10.00"), Category.FOOD, 10));
        Order order = new Order(c);
        order.addLine(p, 1);
        orderRepository.save(order);
        em.flush();
        em.clear();

        // 1. Plain findById
        Order plainOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(Hibernate.isInitialized(plainOrder.getLines())).isFalse();

        em.clear();

        // 2. Query with JOIN FETCH
        List<Order> fetchOrders = orderRepository.findWithLinesByCustomerEmail("test@test.com");
        assertThat(fetchOrders).isNotEmpty();
        assertThat(Hibernate.isInitialized(fetchOrders.get(0).getLines())).isTrue();
    }

    @Test
    @DisplayName("Interface projection selects only projected columns")
    void testProjections() {
        Customer c = customerRepository.save(new Customer("Grace", "grace@test.com",
            new Address("S", "C", "Z"), new Address("S", "C", "Z")));
        Order o = new Order(c);
        o.setStatus(OrderStatus.PAID);
        orderRepository.save(o);
        em.flush();
        em.clear();

        Page<OrderSummary> page = orderRepository.findByStatus(OrderStatus.PAID, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).extracting(s -> s.getCustomer().getName()).contains("Grace");
    }
}
