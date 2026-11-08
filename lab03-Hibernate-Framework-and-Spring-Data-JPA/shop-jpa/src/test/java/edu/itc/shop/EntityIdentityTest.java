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

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EntityIdentityTest {

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Entity remains findable in HashSet across transient and persistent lifecycle transitions")
    void testIdentityInCollections() {
        Set<Product> set = new HashSet<>();
        Product product = new Product("ID-1", "Identity Test", Money.eur("10.00"), Category.TOYS, 5);

        set.add(product);
        assertThat(set).contains(product);

        // Transition from Transient to Managed
        em.persist(product);
        em.flush();

        // Product's ID is now assigned, but hash code remains constant!
        assertThat(product.getId()).isNotNull();
        assertThat(set.contains(product)).isTrue();

        // Reload in detached context
        em.clear();
        Product reloaded = em.find(Product.class, product.getId());

        assertThat(reloaded).isEqualTo(product);
        assertThat(reloaded == product).isFalse(); // Two different Java heap references
        assertThat(set.contains(reloaded)).isTrue();
    }
}
