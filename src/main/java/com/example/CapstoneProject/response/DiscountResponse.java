package com.example.CapstoneProject.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountResponse {
    private String code;
    private Double discountPercentage;
    private LocalDateTime expiryDate;
    private String status;
    private String userId;
}
