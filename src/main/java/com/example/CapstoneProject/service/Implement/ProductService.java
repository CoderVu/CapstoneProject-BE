package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.Request.ProductRequest;
import com.example.CapstoneProject.Request.VariantRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.mapper.ProductMapper;
import com.example.CapstoneProject.model.Image;
import com.example.CapstoneProject.model.Product;
import com.example.CapstoneProject.model.ProductVariant;
import com.example.CapstoneProject.repository.*;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.PaginatedResponse;
import com.example.CapstoneProject.response.ProductResponse;
import com.example.CapstoneProject.service.ImageUploadService;
import com.example.CapstoneProject.service.Interface.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    private  ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ImageUploadService imageUploadService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private ColorRepository colorRepository;



    @Override
    public boolean addProduct(ProductRequest request, List<MultipartFile> images) {
        if (productRepository.existsByProductName(request.getProductName())) {
            return false;
        }
        Product product = productMapper.toProduct(request);
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            try {
                String imageUrl = imageUploadService.uploadImage(image);
                imageUrls.add(imageUrl);
            } catch (IOException e) {
                return false;
            }
        }
        List<Image> imageEntities = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            Image image = new Image();
            image.setUrl(imageUrl);
            image.setProduct(product);
            imageEntities.add(image);
        }
        if (!imageEntities.isEmpty()) {
            product.setMainImage(imageEntities.get(0));
        }
        product.setImages(imageEntities);
        productRepository.save(product);
        return true;
    }

    @Override
    public APIResponse addVariants(String productId, List<VariantRequest> variantRequests) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Product not found")
                    .build();
        }
        for (VariantRequest variantRequest : variantRequests) {
            APIResponse response = productMapper.toProductVariant(variantRequest, product, false);
            if (response.getStatusCode() != Code.CREATED.getCode()) {
                return response;
            }
        }
        productRepository.save(product);
        return APIResponse.builder()
                .statusCode(Code.CREATED.getCode())
                .message("Variants added successfully")
                .build();
    }
    @Override
    public APIResponse updateVariant(String productId, String variantId, VariantRequest variantRequest) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Product not found")
                    .build();
        }

        APIResponse response = productMapper.toProductVariant(variantRequest, product, true);
        if (response.getStatusCode() != Code.OK.getCode()) {
            return response;
        }

        productRepository.save(product);
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Variant updated successfully")
                .build();
    }

    @Override
    public boolean deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }
    @Override
    public PaginatedResponse<ProductResponse> getAllProduct(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductResponse> productResponses = products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
        return new PaginatedResponse<>(
                productResponses,
                products.getTotalPages(),
                products.getTotalElements(),
                products.getNumber(),
                products.getSize()
        );
    }

    @Override
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id).get();
        return productMapper.toProductResponse(product);
    }


}