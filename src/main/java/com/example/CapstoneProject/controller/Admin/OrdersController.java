package com.example.CapstoneProject.controller.Admin;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.IOrderService;
import com.example.CapstoneProject.service.Interface.IProductService;

import lombok.RequiredArgsConstructor;


@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/order")
@RequiredArgsConstructor
public class OrdersController {

    @Autowired
    private IProductService productService;
    @Autowired
    private IOrderService orderService;

    @GetMapping("/get-all")
    public ResponseEntity<APIResponse> getAllOrder(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        APIResponse response = orderService.getAllOrder(pageable);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update-status")
    public ResponseEntity<APIResponse> updateOrderStatus(@RequestParam String orderId, @RequestParam String status) {
        APIResponse response = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/order-statistics")
    public ResponseEntity<APIResponse> getOrderStatistics() {
        APIResponse response = orderService.getOrderStatistics();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}