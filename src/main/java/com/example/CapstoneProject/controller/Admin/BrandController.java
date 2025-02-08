package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.request.BrandRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Implement.BrandService;
import com.example.CapstoneProject.StatusCode.Code;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> addBrand(@RequestBody BrandRequest request) {
        boolean isAdded = brandService.addBrand(request);
        if (isAdded) {
            return ResponseEntity.status(Code.CREATED.getCode())
                    .body(new APIResponse(Code.CREATED.getCode(), Code.CREATED.getMessage(), ""));
        } else {
            return ResponseEntity.status(Code.CONFLICT.getCode())
                    .body(new APIResponse(Code.CONFLICT.getCode(), Code.CONFLICT.getMessage(), ""));
        }
    }
}