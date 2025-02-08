package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id =:productId AND pv.size.sizeId =:sizeId AND pv.color.colorId =:colorId")
    Optional<ProductVariant> findByProductIdAndSizeIdAndColorId(String productId, String sizeId, String colorId);
}
