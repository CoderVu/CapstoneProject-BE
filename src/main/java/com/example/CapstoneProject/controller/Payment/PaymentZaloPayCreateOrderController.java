package com.example.CapstoneProject.controller.Payment;

import com.example.CapstoneProject.request.PaymentRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/zalopay")
public class PaymentZaloPayCreateOrderController {
    private final IPaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<APIResponse> createOrderZaloPay(@RequestBody PaymentRequest orderRequest) throws IOException {
        Map<String, Object> result = this.paymentService.createOrderZaloPay(orderRequest);
        return new ResponseEntity<>(new APIResponse(200, "Create order successfully", result), HttpStatus.OK);
    }
}