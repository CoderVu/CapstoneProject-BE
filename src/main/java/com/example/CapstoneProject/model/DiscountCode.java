package com.example.CapstoneProject.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Data
@Table(name = "discount_codes")
public class DiscountCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String code;
    private double discountPercentage;
    private String status;
    private LocalDateTime expiryDate = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}