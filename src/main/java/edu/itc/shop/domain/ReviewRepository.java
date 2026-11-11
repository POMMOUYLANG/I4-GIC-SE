package edu.itc.shop.domain;

import java.util.List;

public interface ReviewRepository {
    List<Review> findByProductId(long productId);
    Review save(Review review);
}
