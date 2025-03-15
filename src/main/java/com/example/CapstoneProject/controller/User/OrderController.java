package com.example.CapstoneProject.controller.User;

import com.example.CapstoneProject.request.OrderRequest;
import com.example.CapstoneProject.request.RateRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.IOrderService;
import com.example.CapstoneProject.service.Interface.IRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;
    @Autowired
    private IRateService rateService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse> createOrderNow(@RequestHeader("Authorization") String token, @RequestBody OrderRequest request) {
        String newToken = token.substring(7);
        request.setToken(newToken);
        APIResponse response = orderService.createOrderNow(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<APIResponse> createOrderCart(@RequestHeader("Authorization") String token, @RequestBody OrderRequest request) {
        String newToken = token.substring(7);
        request.setToken(newToken);
        APIResponse response = orderService.createOrderFromCart(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/rating")
    public ResponseEntity<APIResponse> rateProduct(
            @RequestHeader("Authorization") String token,
            @RequestParam("orderId") String orderId,
            @RequestParam("rating") Double rating,
            @RequestParam("comment") String comment,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        String newToken = token.substring(7);
        RateRequest request = new RateRequest();
        request.setToken(newToken);
        request.setOrderId(orderId);
        request.setRate(rating);
        request.setComment(comment);
        request.setImages(imageFiles);
        APIResponse response = rateService.rateProduct(request);
        return ResponseEntity.ok(response);
    }
}
