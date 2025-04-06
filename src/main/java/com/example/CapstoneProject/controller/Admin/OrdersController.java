package com.example.CapstoneProject.controller.Admin;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.IOrderService;
import com.example.CapstoneProject.service.Interface.IProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



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

}