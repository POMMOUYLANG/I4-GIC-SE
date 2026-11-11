package edu.itc.shop.domain;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryReviewRepository implements ReviewRepository {

    private final ConcurrentMap<Long, Review> storage = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(0);

    @Override
    public List<Review> findByProductId(long productId) {
        return storage.values().stream()
                .filter(r -> r.productId() == productId)
                .sorted(Comparator.comparingLong(Review::id))
                .toList();
    }

    @Override
    public Review save(Review review) {
        long id = review.id() == 0 ? ids.incrementAndGet() : review.id();
        Review saved = new Review(id, review.productId(), review.stars(), review.author(), review.text());
        storage.put(id, saved);
        return saved;
    }
}
