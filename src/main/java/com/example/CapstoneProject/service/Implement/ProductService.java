package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.*;
import com.example.CapstoneProject.request.ProductRequest;
import com.example.CapstoneProject.request.VariantRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.mapper.ProductMapper;
import com.example.CapstoneProject.repository.*;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.PaginatedResponse;
import com.example.CapstoneProject.response.ProductResponse;
import com.example.CapstoneProject.service.ImageUploadService;
import com.example.CapstoneProject.service.Interface.IProductService;
import com.example.CapstoneProject.service.ProductSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    private  ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CollectionRepository collectionRepository;
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
    public APIResponse addProduct(ProductRequest request, List<MultipartFile> images) {
        Product product = new Product();
        if (productRepository.existsByProductName(request.getProductName())) {
            return APIResponse.builder()
                    .statusCode(Code.CONFLICT.getCode())
                    .message("Product already exists")
                    .build();
        }
        product.setProductName(request.getProductName());
        if (request.getDescription().length() < 10) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Description must be at least 10 characters")
                    .build();
        }
        product.setDescription(request.getDescription());
        if (request.getPrice() < 0) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Price must be greater than 0")
                    .build();
        }
        product.setPrice(request.getPrice());
        if (request.getDiscountPrice() < 0) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Discount price must be greater than 0")
                    .build();
        }
        product.setDiscountPrice(request.getDiscountPrice());
        product.setOnSale(request.getOnSale());
        product.setBestSeller(request.getBestSeller());
        if (request.getGender() == null || request.getGender().isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Không tìm thấy giới tính")
                    .build();
        }
        product.setGender(request.getGender());
        Brand brand = brandRepository.findByName(request.getBrandName());
        if (brand == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Brand not found")
                    .build();
        }
        product.setBrand(brand);
        Category category = categoryRepository.findByName(request.getCategoryName());
        if (category == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Category not found")
                    .build();
        }
        product.setCategory(category);
        product.setNewProduct(request.getNewProduct());

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            try {
                String imageUrl = imageUploadService.uploadImage(image);
                imageUrls.add(imageUrl);
            } catch (IOException e) {
                return APIResponse.builder()
                        .statusCode(Code.INTERNAL_SERVER_ERROR.getCode())
                        .message("Failed to upload image")
                        .build();
            }
        }
        List<Image> imageEntities = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            Image image = new Image();
            image.setUrl(imageUrl);
            image.setProduct(product);
            imageEntities.add(image);
        }
        if (imageEntities.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("No images provided")
                    .build();
        }
        product.setMainImage(imageEntities.get(0));
        product.setImages(imageEntities);
        productRepository.save(product);
        return APIResponse.builder()
                .statusCode(Code.CREATED.getCode())
                .message("Product created successfully")
                .build();
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
    public APIResponse getProductOnSale(){
        List<Product> products = productRepository.findByOnSale(true);
        List<ProductResponse> productResponses = products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Products on sale")
                .data(productResponses)
                .build();
    }

    @Override
    public PaginatedResponse<ProductResponse> getProductsByCollection(String collectionId, Pageable pageable) {
        Optional<Collection> collection = collectionRepository.findById(collectionId);
        if (collection.isEmpty()) {
            return null;
        }
        Page<Product> products = productRepository.findByCollections(collection.get(), pageable);
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
    public PaginatedResponse<ProductResponse> FilterProducts(Pageable pageable, String gender, String category, String brand, Double priceMin, Double priceMax, String color, String size) {
        Specification<Product> spec = Specification.where(null);
        if (gender != null && !gender.isEmpty()) {
            spec = spec.and(ProductSpecification.hasGender(gender));
        }

        if (category != null && !category.isEmpty()) {
            spec = spec.and(ProductSpecification.hasCategory(category));
        }
        if (brand != null && !brand.isEmpty()) {
            spec = spec.and(ProductSpecification.hasBrand(brand));
        }
        if (priceMin != null && priceMax != null) {
            spec = spec.and(ProductSpecification.hasPrice(priceMin, priceMax));
        }
        if (color != null && !color.isEmpty()) {
            spec = spec.and(ProductSpecification.hasColor(color));
        }
        if (size != null && !size.isEmpty()) {
            spec = spec.and(ProductSpecification.hasSize(size));
        }

        Page<Product> products = productRepository.findAll(spec, pageable);
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
    public APIResponse updateProduct(String productId, ProductRequest productRequest, List<MultipartFile> imageFiles) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Product not found")
                    .build();
        }
        if (productRepository.existsByProductName(productRequest.getProductName()) && !product.getProductName().equals(productRequest.getProductName())) {
            return APIResponse.builder()
                    .statusCode(Code.CONFLICT.getCode())
                    .message("Product already exists")
                    .build();
        }
        product.setProductName(productRequest.getProductName());
        if (productRequest.getDescription().length() < 10) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Description must be at least 10 characters")
                    .build();
        }
        product.setDescription(productRequest.getDescription());
        if (productRequest.getPrice() < 0) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Price must be greater than 0")
                    .build();
        }
        product.setPrice(productRequest.getPrice());
        if (productRequest.getDiscountPrice() < 0) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Discount price must be greater than 0")
                    .build();
        }
        product.setDiscountPrice(productRequest.getDiscountPrice());
        product.setOnSale(productRequest.getOnSale());
        product.setBestSeller(productRequest.getBestSeller());
        if (productRequest.getGender() == null || productRequest.getGender().isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Gender not found")
                    .build();
        }
        product.setGender(productRequest.getGender());
        Brand brand = brandRepository.findByName(productRequest.getBrandName());
        if (brand == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Brand not found")
                    .build();
        }
        product.setBrand(brand);
        Category category = categoryRepository.findByName(productRequest.getCategoryName());
        if (category == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Category not found")
                    .build();
        }
        product.setCategory(category);
        product.setNewProduct(productRequest.getNewProduct());

        // Clear existing images to handle orphan removal
        product.getImages().clear();
        productRepository.save(product);

        List<String> imageUrls = new ArrayList<>();
        if (imageFiles != null) {
            for (MultipartFile image : imageFiles) {
                try {
                    String imageUrl = imageUploadService.uploadImage(image);
                    imageUrls.add(imageUrl);
                } catch (IOException e) {
                    return APIResponse.builder()
                            .statusCode(Code.INTERNAL_SERVER_ERROR.getCode())
                            .message("Failed to upload image")
                            .build();
                }
            }
        }
        List<Image> imageEntities = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            Image image = new Image();
            image.setUrl(imageUrl);
            image.setProduct(product);
            imageEntities.add(image);
        }
        if (imageEntities.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("No images provided")
                    .build();
        }
        product.setMainImage(imageEntities.get(0));
        product.setImages(imageEntities);
        productRepository.save(product);
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Product updated successfully")
                .build();
    }


    @Override
    public PaginatedResponse<ProductResponse> getRelatedProducts(String productId, Pageable pageable) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return new PaginatedResponse<>(Collections.emptyList(), 0, 0, pageable.getPageNumber(), pageable.getPageSize());
        }
        List<Product> relatedProducts = productRepository.findRelatedProducts(product.get().getCategory().getId(), productId, pageable);
        List<ProductResponse> productResponses = relatedProducts.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
        return new PaginatedResponse<>(
                productResponses,
                relatedProducts.size() / pageable.getPageSize(),
                relatedProducts.size(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }
    @Override
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id).get();
        return productMapper.toProductResponse(product);
    }


}