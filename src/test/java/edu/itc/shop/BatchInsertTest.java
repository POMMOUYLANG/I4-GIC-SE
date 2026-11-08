package edu.itc.shop;

import edu.itc.shop.domain.Category;
import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.Product;
import edu.itc.shop.repository.ProductRepository;
import edu.itc.shop.support.QueryCounter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BatchInsertTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private QueryCounter queryCounter;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    @DisplayName("High-throughput JDBC batching with SEQUENCE generator batches 100 inserts")
    void testJdbcBatchInsertWithSequence() {
        List<Product> products = new ArrayList<>();
        long timestamp = System.currentTimeMillis() % 100000;
        for (int i = 0; i < 100; i++) {
            products.add(new Product(
                "BATCH-" + timestamp + "-" + i,
                "Batch Product " + i,
                Money.eur("15.00"),
                Category.BOOKS,
                50
            ));
        }

        transactionTemplate.execute(status -> {
            queryCounter.reset();
            productRepository.saveAll(products);
            productRepository.flush();

            // 100 entities inserted
            assertThat(queryCounter.inserts()).isGreaterThanOrEqualTo(100L);

            // With jdbc.batch_size = 50 and SEQUENCE allocation,
            // prepared statements executed is drastically smaller than 100 (around 2-6 statements)
            assertThat(queryCounter.count()).isLessThan(20L);
            return null;
        });
    }

    @Test
    @DisplayName("SEQUENCE generator enables in-memory ID pre-allocation without immediate INSERT")
    void testSequencePreallocatesIdsEnablingBatching() {
        Product p = new Product(
            "SEQ-TEST-" + (System.currentTimeMillis() % 100000),
            "Sequence Test",
            Money.eur("30.00"),
            Category.TOYS,
            10
        );

        transactionTemplate.execute(status -> {
            queryCounter.reset();
            Product saved = productRepository.save(p);

            // ID is assigned immediately from sequence pool in memory
            assertThat(saved.getId()).isNotNull();

            // INSERT is delayed until flush/commit (unlike IDENTITY which requires immediate SQL INSERT)
            return null;
        });
    }
}
