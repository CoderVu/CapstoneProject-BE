package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Color;
import com.example.CapstoneProject.model.Product;
import com.example.CapstoneProject.model.ProductVariant;
import com.example.CapstoneProject.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id =:productId AND pv.size.sizeId =:sizeId AND pv.color.colorId =:colorId")
    Optional<ProductVariant> findByProductIdAndSizeIdAndColorId(@Param("productId") String productId, @Param("sizeId") String sizeId, @Param("colorId") String colorId);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id =:productId AND pv.size.sizeId =:sizeId AND pv.color.colorId =:colorId")
    ProductVariant findByProductAndSizeAndColor(@Param("productId") String productId, @Param("sizeId") String sizeId, @Param("colorId") String colorId);
}