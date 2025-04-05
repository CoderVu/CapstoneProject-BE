package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.request.ProductDescriptionRequest;
import com.example.CapstoneProject.request.ProductRequest;
import com.example.CapstoneProject.request.VariantRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.request.ProductCareInstructionsRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.service.Interface.IProductCareInstructionsService;
import com.example.CapstoneProject.service.Interface.IProductDescriptionService;
import com.example.CapstoneProject.service.Interface.IProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class ProductController {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private IProductService productService;
    @Autowired
    private IProductDescriptionService productDescriptionService;
    @Autowired
    private IProductCareInstructionsService productCareInstructionsService;



    /**
     * Add a new product with details and images
     */
    @PostMapping(value = "/add", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<APIResponse> addProduct(
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("discountPrice") Double discountPrice,
            @RequestParam(value = "onSale", required = false) Boolean onSale,
            @RequestParam(value = "bestSeller", required = false) Boolean bestSeller,
            @RequestParam("categoryName") String categoryName,
            @RequestParam("gender") String gender,
            @RequestParam("brandName") String brandName,
            @RequestParam(value = "newProduct" , required = false) Boolean newProduct,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) {
        try {
            ProductRequest productRequest = ProductRequest.builder()
                    .productName(productName)
                    .description(description)
                    .price(price)
                    .discountPrice(discountPrice)
                    .onSale(onSale)
                    .gender(gender)
                    .bestSeller(bestSeller)
                    .categoryName(categoryName)
                    .brandName(brandName)
                    .newProduct(newProduct)
                    .build();

            APIResponse isAdded = productService.addProduct(productRequest, imageFiles);
            return ResponseEntity.status(isAdded.getStatusCode()).body(isAdded);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), ""));
        }
    }

    @PutMapping(value = "/update/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse> updateProduct(
            @PathVariable String productId,
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("discountPrice") Double discountPrice,
            @RequestParam("onSale") Boolean onSale,
            @RequestParam("bestSeller") Boolean bestSeller,
            @RequestParam("categoryName") String categoryName,
            @RequestParam("gender") String gender,
            @RequestParam("brandName") String brandName,
            @RequestParam("newProduct") Boolean newProduct,
            @RequestParam("imageIds") List<String> imageIds,
            @RequestParam("mainImageId") String mainImageId,
            @RequestParam("colorMap") String colorMapJson, // JSON chứa ánh xạ màu sắc
            @RequestParam(value = "colorImages", required = false) List<MultipartFile> colorImages // Danh sách ảnh
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Chuyển đổi colorMap JSON thành Map<String, String> (imageId -> color)
            Map<String, String> colorMap = objectMapper.readValue(colorMapJson, new TypeReference<>() {});

            // Chuyển `colorImages` từ List sang Map<String, MultipartFile[]>
            Map<String, MultipartFile[]> colorImagesMap = new HashMap<>();
            if (colorImages != null) {
                for (MultipartFile file : colorImages) {
                    String fileName = file.getOriginalFilename();
                    String color = productService.extractColorFromFileName(fileName, colorMap);
                    colorImagesMap.computeIfAbsent(color, k -> new MultipartFile[0]);
                    colorImagesMap.put(color, productService.appendToArray(colorImagesMap.get(color), file));
                }
            }

            // Tạo ProductRequest
            ProductRequest productRequest = ProductRequest.builder()
                    .productName(productName)
                    .description(description)
                    .price(price)
                    .discountPrice(discountPrice)
                    .onSale(onSale)
                    .gender(gender)
                    .bestSeller(bestSeller)
                    .categoryName(categoryName)
                    .brandName(brandName)
                    .newProduct(newProduct)
                    .imageIds(imageIds)
                    .mainImageId(mainImageId)
                    .colorMap(colorMap) // Truyền colorMap vào
                    .build();

            APIResponse isUpdated = productService.updateProduct(productId, productRequest, colorImagesMap);

            return ResponseEntity.status(isUpdated.getStatusCode()).body(isUpdated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error: " + e.getMessage(), ""));
        }
    }



    @PostMapping(value = "/{productId}/variants", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> addVariants(
            @PathVariable String productId,
            @RequestBody @Valid List<VariantRequest> variantRequests
    ) {
        try {
            APIResponse response = productService.addVariant(productId, variantRequests);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), ""));
        }
    }
    @PutMapping(value = "/{productId}/variants", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> updateVariant(
            @PathVariable String productId,
            @RequestBody @Valid VariantRequest variantRequest
    ) {
        try {
            APIResponse response = productService.updateVariant(productId, variantRequest);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), ""));
        }
    }
    @DeleteMapping(value = "/variants/{variantId}", produces = "application/json")
    public ResponseEntity<APIResponse> deleteVariant(
            @PathVariable String variantId

    ) {
        try {
            APIResponse response = productService.deleteVariant(variantId);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), ""));
        }
    }

    @PostMapping(value = "/{productId}/description", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> addProductDescription(
            @PathVariable String productId,
            @RequestBody @Valid ProductDescriptionRequest productDescriptionRequest
    ) {
        try {
            productDescriptionRequest.setProductId(productId);
            APIResponse response = productDescriptionService.addProductDescription(productDescriptionRequest);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), ""));
        }
    }
    @PutMapping(value = "/{productId}/description", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> updateProductDescription(
            @PathVariable String productId,
            @RequestBody @Valid ProductDescriptionRequest productDescriptionRequest
    ) {
        try {
            productDescriptionRequest.setProductId(productId);
            APIResponse response = productDescriptionService.updateProductDescription(productDescriptionRequest);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), ""));
        }
    }
    @PostMapping(value = "/{productId}/careInstruction", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> addProductCareInstruction(
        @PathVariable String productId, @RequestBody ProductCareInstructionsRequest request) {
        try {
            request.setProductId(productId);
            APIResponse response = productCareInstructionsService.addProductCareInstructions(request);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), ""));
        }

       
    }
    @PutMapping(value = "/{productId}/careInstruction", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> updateProductCareInstruction(
        @PathVariable String productId, @RequestBody ProductCareInstructionsRequest request) {
        try {
            request.setProductId(productId);
            APIResponse response = productCareInstructionsService.updateProductCareInstructions(request);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), ""));
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
                    .body(new APIResponse(Code.OK.getCode(), "Product deleted successfully", ""))
                    : ResponseEntity.status(Code.NOT_FOUND.getCode())
                    .body(new APIResponse(Code.NOT_FOUND.getCode(), "Product not found", ""));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), ""));
        }
    }
}
