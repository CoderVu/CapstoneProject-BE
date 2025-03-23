package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.PaymentRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface IPaymentService {
    Map<String, Object> createOrderZaloPay(PaymentRequest orderRequest) throws IOException;


    Map<String, Object> getStatusZaloPay(PaymentRequest requestDTO) throws IOException, URISyntaxException;
}
