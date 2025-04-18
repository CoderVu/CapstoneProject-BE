package com.example.CapstoneProject.controller.Public;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.response.*;
import com.example.CapstoneProject.service.Interface.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    private IProductDescriptionService productDescriptionService;
    @Autowired
    private IProductCareInstructionsService productCareInstructionsService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IBrandService brandService;
    @Autowired
    private IColorService colorService;
    @Autowired
    private ISizeService sizeService;
    @Autowired
    private IRateService rateService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IDiscountCodeService discountCodeService;

    @GetMapping("/products")
    public ResponseEntity<APIResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<ProductResponse> productResponses = productService.getAllProduct(pageable);
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), productResponses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/filter")
    public ResponseEntity<APIResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String categoryProduct,
            @RequestParam(required = false) String brandProduct,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) String colorProduct,
            @RequestParam(required = false) String sizeProduct) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<ProductResponse> productResponses = productService.FilterProducts(pageable, gender, categoryProduct, brandProduct, priceMin, priceMax, colorProduct, sizeProduct);
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), productResponses);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products/collection/{id}")
    public ResponseEntity<APIResponse> getProductsByCollectionId(
        @PathVariable String id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<ProductResponse> productResponses = productService.getProductsByCollection(id, pageable);
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), productResponses);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<APIResponse> getProductById(@PathVariable String id) {
        ProductResponse productResponse = productService.getProductById(id);
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), productResponse);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products/sale")
    public ResponseEntity<APIResponse> getProductOnSale() {
        APIResponse response = productService.getProductOnSale();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/categories")
    public ResponseEntity<APIResponse> getAllCategories() {
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), categoryService.getAllCategory());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/brands")
    public ResponseEntity<APIResponse> getAllBrands() {
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), brandService.getAllBrand());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/colors")
    public ResponseEntity<APIResponse> getAllColors() {
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), colorService.getAllColor());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/sizes")
    public ResponseEntity<APIResponse> getAllSizes() {
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), sizeService.getAllSizes());
        return ResponseEntity.ok(response);
    }
    @GetMapping(value = "/products/{productId}/description", produces = "application/json")
    public ResponseEntity<APIResponse> getProductDescription(@PathVariable String productId) {
        try {
            APIResponse response = productDescriptionService.getProductDescription(productId);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), ""));
        }
    }
    @GetMapping(value = "/products/{productId}/careInstruction", produces = "application/json")
    public ResponseEntity<APIResponse> getProductCareInstruction(@PathVariable String productId) {
        try {
            APIResponse response = productCareInstructionsService.getProductCareInstructions(productId);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(Code.INTERNAL_SERVER_ERROR.getCode())
                    .body(new APIResponse(Code.INTERNAL_SERVER_ERROR.getCode(), "Internal server error: " + e.getMessage(), ""));
        }
    }
    @GetMapping("/products/rated/{id}")
    public ResponseEntity<APIResponse> getRatedProducts(@PathVariable String id,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "30") int size) {
            Pageable pageable = PageRequest.of(page, size);
            PaginatedResponse <RateResponse> rateResponsePaginatedResponse = rateService.getRates(pageable, id);
            APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), rateResponsePaginatedResponse);
            return ResponseEntity.ok(response);

    }
    @GetMapping("/users/{id}")
    public ResponseEntity<APIResponse> getUserById(@PathVariable String id) {
        UserResponse userResponse = userService.getUserInfoById(id);
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), userResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/recent")
    public ResponseEntity<APIResponse> getRecentOrders() {
        APIResponse response = orderService.getOrdersWithinLastHour();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/products/{id}/view")
    public ResponseEntity<APIResponse> viewProduct(@PathVariable String id, @CookieValue(value = "viewedProducts", defaultValue = "") String viewedProducts, HttpServletResponse response) {
        ProductResponse productResponse = productService.getProductById(id);
        if (productResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse(HttpStatus.NOT_FOUND.value(), "Product not found", null));
        }
        if (!viewedProducts.contains(id)) {
            if (!viewedProducts.isEmpty()) {
                viewedProducts += "," + id;
            } else {
                viewedProducts = id;
            }
        }
        String encodedViewedProducts = URLEncoder.encode(viewedProducts, StandardCharsets.UTF_8);

        Cookie viewedProductsCookie = new Cookie("viewedProducts", encodedViewedProducts);
        viewedProductsCookie.setPath("/");
        viewedProductsCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(viewedProductsCookie);

        APIResponse apiResponse = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), productResponse);
        return ResponseEntity.ok(apiResponse);
    }
    @GetMapping("/products/viewed")
    public ResponseEntity<APIResponse> getViewedProducts(@CookieValue(value = "viewedProducts", defaultValue = "") String viewedProducts) {
        if (viewedProducts.isEmpty()) {
            return ResponseEntity.ok(new APIResponse(Code.OK.getCode(), "No viewed products", Collections.emptyList()));
        }
        String[] productIds = viewedProducts.split(",");
        List<ProductResponse> productResponses = new ArrayList<>();
        for (String productId : productIds) {
            ProductResponse productResponse = productService.getProductById(productId);
            if (productResponse != null) {
                productResponses.add(productResponse);
            }
        }

        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), productResponses);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products/related/{id}")
     public ResponseEntity<APIResponse> getRelatedProducts(@PathVariable String id,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<ProductResponse> productResponses = productService.getRelatedProducts(id, pageable);
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), productResponses);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products/color/{productId}")
    public ResponseEntity<APIResponse> getColorsByProductId(@PathVariable String productId) {
        APIResponse response = productService.getColorByProductId(productId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/users/all-chat")
    public ResponseEntity<APIResponse> getAllChatUsers() {
        List<UserResponse> users = userService.getAllUser();
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), users);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/images")
    public ResponseEntity<APIResponse> getAllImages() {
        APIResponse response = productService.getAllImages();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products/images")
    public ResponseEntity<APIResponse> getProductsByImgUrls(@RequestParam List<String> imgUrls,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<ProductResponse> productResponses = productService.getProductByImgUrl(imgUrls, pageable);
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), productResponses);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/products/search")
    public ResponseEntity<APIResponse> searchProducts(@RequestParam String keyword,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<ProductResponse> productResponses = productService.SearchProducts(pageable, keyword);
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), productResponses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all")
    public ResponseEntity<APIResponse> getAllDiscountCodes() {
        APIResponse response = discountCodeService.getAllDiscountCodes();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}