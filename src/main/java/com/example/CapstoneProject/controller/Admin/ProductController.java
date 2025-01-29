package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.Request.ProductRequest;
import com.example.CapstoneProject.Request.VariantRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.service.Interface.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    /**
     * Add a new product with details and images
     */
    @PostMapping(value = "/add", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<APIResponse> addProduct(
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") Integer price,
            @RequestParam("categoryName") String categoryName,
            @RequestParam("brandName") String brandName,
            @RequestParam("newProduct") Boolean newProduct,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) {
        try {
            ProductRequest productRequest = ProductRequest.builder()
                    .productName(productName)
                    .description(description)
                    .price(price)
                    .categoryName(categoryName)
                    .brandName(brandName)
                    .newProduct(newProduct)
                    .build();

            boolean isAdded = productService.addProduct(productRequest, imageFiles);
            return isAdded
                    ? ResponseEntity.status(Code.CREATED.getCode())
                    .body(new APIResponse(Code.CREATED.getCode(), Code.CREATED.getMessage(), true))
                    : ResponseEntity.status(Code.CONFLICT.getCode())
                    .body(new APIResponse(Code.CONFLICT.getCode(), "Product already exists", false));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), false));
        }
    }
    @PostMapping(value = "/{productId}/variants", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> addVariants(
            @PathVariable String productId,
            @RequestBody @Valid List<VariantRequest> variantRequests
    ) {
        try {
            APIResponse response = productService.addVariants(productId, variantRequests);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), false));
        }
    }
    @PutMapping(value = "/{productId}/variants", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> updateVariant(
            @PathVariable String productId,
            @RequestBody @Valid VariantRequest variantRequest
    ) {
        try {
            APIResponse response = productService.updateVariant(productId, variantRequest.getId(), variantRequest);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), false));
        }
    }
    /**
     * Delete a product by its ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteProduct(@PathVariable String id) {
        try {
            boolean isDeleted = productService.deleteProduct(id);

            return isDeleted
                    ? ResponseEntity.status(Code.OK.getCode())
                    .body(new APIResponse(Code.OK.getCode(), "Product deleted successfully", true))
                    : ResponseEntity.status(Code.NOT_FOUND.getCode())
                    .body(new APIResponse(Code.NOT_FOUND.getCode(), "Product not found", false));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), false));
        }
    }
}
