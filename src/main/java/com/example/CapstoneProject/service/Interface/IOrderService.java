package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.PaymentRequest;
import com.example.CapstoneProject.response.APIResponse;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface IOrderService {
    APIResponse createOrderNow(PaymentRequest request) throws IOException;

    APIResponse createOrderFromCart(PaymentRequest request) throws IOException;

    String generateUniqueOrderCode();

    APIResponse cancelOrder(String token, String orderId);

    APIResponse getHistoryOrder(String token);

    APIResponse getAllOrder(Pageable pageable);

    APIResponse updateOrderStatus(String orderId, String status);

    APIResponse getOrdersWithinLastHour();

    Integer getTotalSoldByProductId(String productId);

    APIResponse getOrderStatistics();
}
