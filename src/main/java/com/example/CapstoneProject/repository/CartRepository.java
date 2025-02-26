package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Cart;
import com.example.CapstoneProject.model.ProductVariant;
import com.example.CapstoneProject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, String> {
    @Query("SELECT c FROM Cart c WHERE c.user =:user AND c.productVariant =:productVariant")
    Cart findByUserAndProductVariant(User user, ProductVariant productVariant);
    Page<Cart> findByUser(User user, Pageable pageable);
}
