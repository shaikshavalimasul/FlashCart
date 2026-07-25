package com.flashcart.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import com.flashcart.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends  JpaRepository<Product,Long> {
    List<Product> findByFlashSaleActive(boolean flashSaleActive);
    List<Product> findByStockGreaterThan(Integer stock);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    @Query("SELECT p FROM Product p " +
            "WHERE p.flashSaleActive = true " +
            "AND p.stock > 0 " +
            "ORDER BY p.price ASC")
    List<Product> findActiveSaleProductsInStock();


    @Query(value = "SELECT * FROM products " +
            "WHERE stock < :threshold " +
            "ORDER BY stock ASC",
            nativeQuery = true)
    List<Product> findLowStockProducts(
            @Param("threshold") Integer threshold);
}
