package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    @Query("SELECT p FROM Product p WHERE p.productName =:productName")
    Product findByName(String productName);

    boolean existsByProductName(String productName);
}
