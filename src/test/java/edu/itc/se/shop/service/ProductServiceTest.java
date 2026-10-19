package edu.itc.se.shop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.itc.se.shop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    ProductService service;

    @Autowired
    ProductRepository repo;

    @Test
    void restockRollsBackWhenSourceHasTooLittleStock() {
        // Product 1 initially has 10 units in data.sql
        int before1 = repo.findById(1).orElseThrow().stock();
        // Product 2 initially has 15 units in data.sql
        int before2 = repo.findById(2).orElseThrow().stock();

        // Attempt to transfer 1,000 units (far exceeding available stock of 10)
        assertThrows(IllegalStateException.class, () -> service.restock(1, 2, 1_000));

        // Verify that BOTH product stocks remained completely unchanged
        assertEquals(before1, repo.findById(1).orElseThrow().stock(), "Source stock must be rolled back");
        assertEquals(before2, repo.findById(2).orElseThrow().stock(), "Destination stock must be unchanged");
    }
}
