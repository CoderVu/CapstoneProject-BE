package com.example.CapstoneProject.controller.Payment;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.request.PaymentRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/zalopay")
public class PaymentZaloPayCheckStatusController {
    private final IPaymentService paymentService;

    @GetMapping("/get-status")
    public ResponseEntity<APIResponse> getStatus(@RequestParam String apptransid) {
        try {
            // Create a PaymentZaloPayRequest object
            PaymentRequest requestDTO = new PaymentRequest();
            requestDTO.setApptransid(apptransid);
            // Call the service method with the requestDTO
            Map<String, Object> result = this.paymentService.getStatusZaloPay(requestDTO);
            return new ResponseEntity<>(new APIResponse(200, "Order status retrieved successfully", result), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new APIResponse(400, "Failed to retrieve order status", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}