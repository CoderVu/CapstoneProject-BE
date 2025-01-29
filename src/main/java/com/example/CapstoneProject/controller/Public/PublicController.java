package com.example.CapstoneProject.controller.Public;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.PaginatedResponse;
import com.example.CapstoneProject.response.ProductResponse;
import com.example.CapstoneProject.service.Interface.ICategoryService;
import com.example.CapstoneProject.service.Interface.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {
    @Autowired
    private IProductService productService;
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/products")
    public ResponseEntity<APIResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<ProductResponse> productResponses = productService.getAllProduct(pageable);
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), productResponses);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<APIResponse> getProductById(@PathVariable String id) {
        ProductResponse productResponse = productService.getProductById(id);
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), productResponse);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/categories")
    public ResponseEntity<APIResponse> getAllCategories() {
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), categoryService.getAllCategory());
        return ResponseEntity.ok(response);
    }


}