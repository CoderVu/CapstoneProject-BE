package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.request.BrandRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Implement.BrandService;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.service.Interface.IBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/brands")
@RequiredArgsConstructor
public class BrandController {
    @Autowired
    private IBrandService brandService;

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
    @GetMapping(value = "/getAll", produces = "application/json")
    public ResponseEntity<APIResponse> getAllBrand() {
        return ResponseEntity.ok(new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), brandService.getAllBrand()));
    }
    @PutMapping(value = "/update/{brandId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> updateBrand(@PathVariable String brandId, @RequestBody BrandRequest request) {
        request.setBrandId(brandId);
        boolean isUpdated = brandService.updateBrand(request);
        if (isUpdated) {
            return ResponseEntity.status(Code.OK.getCode())
                    .body(new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), ""));
        } else {
            return ResponseEntity.status(Code.NOT_FOUND.getCode())
                    .body(new APIResponse(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(), ""));
        }
    }
    @DeleteMapping(value = "/delete/{brandId}", produces = "application/json")
    public ResponseEntity<APIResponse> deleteBrand(@PathVariable String brandId) {
        boolean isDeleted = brandService.deleteBrand(brandId);
        if (isDeleted) {
            return ResponseEntity.status(Code.OK.getCode())
                    .body(new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), ""));
        } else {
            return ResponseEntity.status(Code.NOT_FOUND.getCode())
                    .body(new APIResponse(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(), ""));
        }
    }
}