package edu.itc.shop;

import edu.itc.shop.domain.Tag;
import edu.itc.shop.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class FlushOrderTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate.execute(status -> {
            tagRepository.findByName("unique-test-tag").ifPresent(tagRepository::delete);
            tagRepository.flush();
            tagRepository.save(new Tag("unique-test-tag"));
            return null;
        });
    }

    @Test
    void testUniqueConstraintViolationDueToFlushOrdering() {
        // Because Hibernate executes EntityInsertAction BEFORE EntityDeleteAction in its ActionQueue,
        // inserting a new tag with the same unique name before deleting the old one triggers a DataIntegrityViolationException.
        assertThatThrownBy(() -> {
            transactionTemplate.execute(status -> {
                Tag oldTag = tagRepository.findByName("unique-test-tag").orElseThrow();
                tagRepository.delete(oldTag);
                tagRepository.save(new Tag("unique-test-tag"));
                tagRepository.flush(); // Triggers ActionQueue execution
                return null;
            });
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void testUniqueConstraintResolvedByFlushingBeforeInsert() {
        // By explicitly invoking flush() after delete, Hibernate executes the SQL DELETE immediately,
        // allowing the subsequent INSERT to succeed without unique constraint collision.
        transactionTemplate.execute(status -> {
            Tag oldTag = tagRepository.findByName("unique-test-tag").orElseThrow();
            tagRepository.delete(oldTag);
            tagRepository.flush(); // Forces SQL DELETE prior to insert

            tagRepository.save(new Tag("unique-test-tag"));
            tagRepository.flush();
            return null;
        });

        Tag activeTag = tagRepository.findByName("unique-test-tag").orElseThrow();
        assertThat(activeTag.getName()).isEqualTo("unique-test-tag");
    }
}
