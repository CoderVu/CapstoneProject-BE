package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.DiscountCode;
import com.example.CapstoneProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, String> {
    @Query("SELECT d FROM DiscountCode d WHERE d.code = :code")
    Optional<DiscountCode> findByCode(String code);
    @Query("SELECT d FROM DiscountCode d WHERE d.user = :user")
    List<DiscountCode> findByUser(User user);
}