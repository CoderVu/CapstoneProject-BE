package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, String> {
    @Query(nativeQuery = true, value = "SELECT * FROM otp WHERE email = ?1 AND otp = ?2 ORDER BY created_at DESC LIMIT 1")
    Optional<OTP> findTopByEmailAndOtpOrderByCreatedAtDesc(String email, String inputOtp);
}
