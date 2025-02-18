package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.ProductDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductDescriptionRepository extends JpaRepository<ProductDescription, String> {
    boolean existsByProductId(String productId);
    @Query("SELECT pd FROM ProductDescription pd WHERE pd.product.id =:productId")
    ProductDescription findByProductId(String productId);
}
