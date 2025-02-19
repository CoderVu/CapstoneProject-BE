package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Product;
import com.example.CapstoneProject.model.Rate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RateRepository extends JpaRepository<Rate, String> {
    @Query("SELECT r FROM Rate r WHERE r.product =:product AND r.userId =:userId")
    boolean existsByProductAndUser(Product product, String userId);
    @Query("SELECT r FROM Rate r WHERE r.product.id =:productId")
    Page<Rate> findByProductId(Pageable pageable, String productId);
}
