package edu.itc.shop;

import edu.itc.shop.domain.Category;
import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=validate"
})
class ProductMappingTest {

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Product entity correctly persists, flushes and round-trips Money and Category")
    void persistAndReloadProduct() {
        Product p = new Product("KB-1", "Mechanical Keyboard", Money.eur("89.90"), Category.HARDWARE, 25);
        Long id = em.persistAndGetId(p, Long.class);

        em.flush();
        em.clear(); // Clear 1st-level cache to force an actual SQL SELECT

        Product reloaded = em.find(Product.class, id);
        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getSku()).isEqualTo("KB-1");
        assertThat(reloaded.getPrice().amount()).isEqualByComparingTo(new BigDecimal("89.90"));
        assertThat(reloaded.getPrice().currency()).isEqualTo("EUR");
        assertThat(reloaded.getCategory()).isEqualTo(Category.HARDWARE);

        // Native SQL verification that Category is stored as string 'HARDWARE', not ordinal 1
        String rawCategory = (String) em.getEntityManager()
            .createNativeQuery("SELECT category FROM product WHERE id = :id")
            .setParameter("id", id)
            .getSingleResult();
        assertThat(rawCategory).isEqualTo("HARDWARE");
    }
}
