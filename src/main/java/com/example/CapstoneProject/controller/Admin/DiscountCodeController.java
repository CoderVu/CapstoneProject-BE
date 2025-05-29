package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.model.DiscountCode;
import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.IDiscountCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/discount-code")
@RequiredArgsConstructor
public class DiscountCodeController {

    @Autowired
    private IDiscountCodeService discountCodeService;


    @PostMapping("/add")
    public ResponseEntity<APIResponse> addDiscountCode(@RequestParam Double discountPercentage, @RequestParam LocalDateTime expiryDate) {
        APIResponse response = discountCodeService.createRandomDiscountCode(discountPercentage, expiryDate);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/get-all")
    public ResponseEntity<APIResponse> getAllDiscountCodes() {
        APIResponse response = discountCodeService.getAllDiscountCodes();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PostMapping("/apply")
    public ResponseEntity<APIResponse> applyDiscountCodeToUser(
            @RequestParam String discountCode,
            @RequestParam String userId) {
        APIResponse response = discountCodeService.applyDiscountCodeToUser(discountCode, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<APIResponse> deleteDiscountCode(@RequestParam String discountCode) {
        APIResponse response = discountCodeService.deleteDiscountCode(discountCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
