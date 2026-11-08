package edu.itc.shop.repository;

import edu.itc.shop.domain.Category;
import edu.itc.shop.domain.Product;
import edu.itc.shop.web.dto.ProductView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findTop3ByCategoryOrderByPriceAmountDesc(Category category);

    List<Product> findByTagsName(String tagName);

    Page<ProductView> findByCategory(Category category, Pageable pageable);

    @Query(value = "SELECT p.* FROM product p WHERE p.stock < :min ORDER BY p.stock", nativeQuery = true)
    List<Product> findLowStock(@Param("min") int min);
}
