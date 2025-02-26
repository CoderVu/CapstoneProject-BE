package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Collection;
import com.example.CapstoneProject.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    @Query("SELECT p FROM Product p WHERE p.productName =:productName")
    Product findByName(String productName);

    boolean existsByProductName(String productName);

    Page<Product> findByCollections(Collection collection, Pageable pageable);

    List<Product> findByOnSale(boolean onSale);


    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :productId")
    List<Product> findRelatedProducts(@Param("categoryId") String categoryId, @Param("productId") String productId, Pageable pageable);
}
