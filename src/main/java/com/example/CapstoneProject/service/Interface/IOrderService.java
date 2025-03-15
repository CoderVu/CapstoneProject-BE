package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.OrderRequest;
import com.example.CapstoneProject.response.APIResponse;

public interface IOrderService {
    APIResponse createOrderNow(OrderRequest request);

    APIResponse createOrderFromCart(OrderRequest request);
}
