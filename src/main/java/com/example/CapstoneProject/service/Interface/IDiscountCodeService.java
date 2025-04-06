package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.response.APIResponse;

import java.time.LocalDateTime;

public interface IDiscountCodeService {
    APIResponse createRandomDiscountCode(double discountPercentage, LocalDateTime expiryDate);

    APIResponse getAllDiscountCodes();

    APIResponse getDiscountCodesByUser(String token);

    APIResponse applyDiscountCodeToUser(String discountCode, String userId);
}
