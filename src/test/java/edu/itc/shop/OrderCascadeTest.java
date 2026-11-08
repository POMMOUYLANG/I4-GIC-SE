package edu.itc.shop;

import edu.itc.shop.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=validate")
class OrderCascadeTest {

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Persist order cascades lines with 1 + N inserts; orphanRemoval executes delete")
    void testCascadeAndOrphanRemoval() {
        Customer customer = new Customer("Ada Lovelace", "ada@example.com",
            new Address("St 1", "London", "W1"), new Address("St 1", "London", "W1"));
        em.persist(customer);

        Product p1 = new Product("BK-1", "Domain-Driven Design", Money.eur("45.00"), Category.BOOKS, 10);
        Product p2 = new Product("BK-2", "Clean Architecture", Money.eur("35.00"), Category.BOOKS, 15);
        em.persist(p1);
        em.persist(p2);

        Order order = new Order(customer);
        order.addLine(p1, 2);
        order.addLine(p2, 1);

        // SQL log shows:
        // 1. insert into orders (...)
        // 2. insert into order_line (...)
        // 3. insert into order_line (...)
        // NO updates!
        em.persist(order);
        em.flush();
        em.clear();

        Order reloaded = em.find(Order.class, order.getId());
        assertThat(reloaded.getLines()).hasSize(2);
        assertThat(reloaded.total().amount()).isEqualByComparingTo("125.00");

        // Orphan removal test: remove first line and flush
        OrderLine lineToRemove = reloaded.getLines().get(0);
        reloaded.removeLine(lineToRemove);
        em.flush();

        Long lineCount = em.getEntityManager()
            .createQuery("select count(l) from OrderLine l where l.order.id = :orderId", Long.class)
            .setParameter("orderId", reloaded.getId())
            .getSingleResult();
        assertThat(lineCount).isEqualTo(1L);

        // Cascade DELETE on entire order
        em.remove(reloaded);
        em.flush();

        Long remainingLines = em.getEntityManager()
            .createQuery("select count(l) from OrderLine l", Long.class)
            .getSingleResult();
        assertThat(remainingLines).isEqualTo(0L);
    }
}
