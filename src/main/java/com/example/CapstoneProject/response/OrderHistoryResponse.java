package com.example.CapstoneProject.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistoryResponse {
    private String orderCode;
    private String userName;
    private LocalDateTime orderDate;
    private String status;
    private String deliveryAddress;
    private String deliveryPhone;
    private double totalAmount;
    private List<OrderDetailResponse> orderDetails;
    private Boolean isFeedback;
}