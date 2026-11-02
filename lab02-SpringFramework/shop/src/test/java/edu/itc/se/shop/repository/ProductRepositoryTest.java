package edu.itc.se.shop.repository;

import static org.junit.jupiter.api.Assertions.*;

import edu.itc.se.shop.domain.Product;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;

/**
 * @JdbcTest configures an in-memory database and JdbcClient,
 * and rolls back after each test method automatically!
 */
@JdbcTest
@Import(ProductRepository.class)
class ProductRepositoryTest {

    @Autowired
    ProductRepository repo;

    @Test
    void shouldFindAllAndInsertProduct() {
        assertEquals(5, repo.findAll().size());

        Product newProduct = new Product(null, "USB-C Hub", new BigDecimal("35.00"), 25);
        long newId = repo.insert(newProduct);
        assertTrue(newId > 0);

        Optional<Product> found = repo.findById(newId);
        assertTrue(found.isPresent());
        assertEquals("USB-C Hub", found.get().name());
    }

    @Test
    void insertingDuplicateNameShouldThrowDuplicateKeyException() {
        // "MacBook Pro 16\"" already exists in data.sql
        Product duplicate = new Product(null, "MacBook Pro 16\"", new BigDecimal("2000.00"), 5);

        assertThrows(DuplicateKeyException.class, () -> repo.insert(duplicate));
    }
}
