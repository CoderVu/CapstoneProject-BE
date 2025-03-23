package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.PaymentRequest;
import com.example.CapstoneProject.response.APIResponse;

public interface IOrderService {
    APIResponse createOrderNow(PaymentRequest request);

    APIResponse createOrderFromCart(PaymentRequest request);

    String generateUniqueOrderCode();

    APIResponse getHistoryOrder(String token);
}
