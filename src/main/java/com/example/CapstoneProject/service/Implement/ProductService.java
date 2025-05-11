package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.*;
import com.example.CapstoneProject.model.Collection;
import com.example.CapstoneProject.request.ProductRequest;
import com.example.CapstoneProject.request.VariantRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.mapper.ProductMapper;
import com.example.CapstoneProject.repository.*;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.ImageResponse;
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
import java.util.*;
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
    @Autowired
    private ProductVariantRepository productVariantRepository;

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
                    .message("Gender not found")
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

        // Handle color-specific images
        if (request.getColorImages() != null) {
            for (Map.Entry<String, List<String>> entry : request.getColorImages().entrySet()) {
                String color = entry.getKey();
                List<String> colorImageUrls = entry.getValue();
                for (String colorImageUrl : colorImageUrls) {
                    Image image = new Image();
                    image.setUrl(colorImageUrl);
                    image.setColor(color);
                    image.setProduct(product);
                    product.getImages().add(image);
                }
            }
        }

        productRepository.save(product);
        return APIResponse.builder()
                .statusCode(Code.CREATED.getCode())
                .message("Product created successfully")
                .build();
    }

    @Override
    public APIResponse addVariant(String productId, List<VariantRequest> variantRequests) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Product not found")
                    .build();
        }

        for (VariantRequest variantRequest : variantRequests) {
            Size size = sizeRepository.findByName(variantRequest.getSizeName());
            if (size == null) {
                return APIResponse.builder()
                        .statusCode(Code.NOT_FOUND.getCode())
                        .message("Size không tồn tại")
                        .build();
            }

            Color color = colorRepository.findByColor(variantRequest.getColorName());
            if (color == null) {
                return APIResponse.builder()
                        .statusCode(Code.NOT_FOUND.getCode())
                        .message("Color không tồn tại")
                        .build();
            }

            if (variantRequest.getQuantity() < 0) {
                return APIResponse.builder()
                        .statusCode(Code.BAD_REQUEST.getCode())
                        .message("Quantity phải lớn hơn 0")
                        .build();
            }

            ProductVariant existingVariant = product.getVariants().stream()
                    .filter(v -> v.getSize().getName().equals(size.getName()) && v.getColor().getColor().equals(color.getColor()))
                    .findFirst()
                    .orElse(null);

            if (existingVariant != null) {
                existingVariant.setQuantity(existingVariant.getQuantity() + variantRequest.getQuantity());
            } else {
                ProductVariant newVariant = new ProductVariant();
                newVariant.setSize(size);
                newVariant.setColor(color);
                newVariant.setQuantity(variantRequest.getQuantity());
                newVariant.setStatus("AVAILABLE");
                newVariant.setProduct(product);
                product.getVariants().add(newVariant);
            }
        }

        productRepository.save(product);
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Thêm variant mới thành công")
                .build();
    }

    @Override
    public APIResponse updateVariant(String productId, VariantRequest variantRequest) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Product not found")
                    .build();
        }

        Size size = sizeRepository.findByName(variantRequest.getSizeName());
        if (size == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Size không tồn tại")
                    .build();
        }

        Color color = colorRepository.findByColor(variantRequest.getColorName());
        if (color == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Color không tồn tại")
                    .build();
        }

        if (variantRequest.getQuantity() < 0) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Quantity phải lớn hơn 0")
                    .build();
        }


        ProductVariant existingVariant = product.getVariants().stream()
                .filter(v -> v.getSize().getName().equals(size.getName()) && v.getColor().getColor().equals(color.getColor()))
                .findFirst()
                .orElse(null);

        if (existingVariant != null) {
            existingVariant.setQuantity(variantRequest.getQuantity());
            productRepository.save(product);
            return APIResponse.builder()
                    .statusCode(Code.OK.getCode())
                    .message("Cập nhật số lượng variant thành công")
                    .build();
        } else {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Variant không tồn tại")
                    .build();
        }
    }
    @Override
    public APIResponse deleteVariant(String variantId) {
        ProductVariant productVariant = productVariantRepository.findById(variantId).orElse(null);
        if (productVariant == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Variant không tồn tại")
                    .build();
        }
        productVariantRepository.delete(productVariant);
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Xóa variant thành công")
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
    public PaginatedResponse<ProductResponse> SearchProducts(Pageable pageable, String keyword) {
        Specification<Product> spec = Specification.where(ProductSpecification.hasKeyword(keyword));
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
    public APIResponse updateProduct(String productId, ProductRequest productRequest, Map<String, MultipartFile[]> colorImages) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Không tìm thấy sản phẩm")
                    .build();
        }

        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setDiscountPrice(productRequest.getDiscountPrice());
        product.setOnSale(productRequest.getOnSale());
        product.setBestSeller(productRequest.getBestSeller());
        product.setGender(productRequest.getGender());

        Brand brand = brandRepository.findByName(productRequest.getBrandName());
        if (brand == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Brand không tồn tại")
                    .build();
        }
        product.setBrand(brand);

        Category category = categoryRepository.findByName(productRequest.getCategoryName());
        if (category == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Category không tồn tại")
                    .build();
        }
        product.setCategory(category);
        product.setNewProduct(productRequest.getNewProduct());

        // Cập nhật màu sắc cho ảnh đã có
        if (productRequest.getColorMap() != null) {
            for (Map.Entry<String, String> entry : productRequest.getColorMap().entrySet()) {
                String imageId = entry.getKey();
                String color = entry.getValue();

                Image image = product.getImages().stream()
                        .filter(img -> img.getId().equals(imageId))
                        .findFirst()
                        .orElse(null);

                if (image != null) {
                    image.setColor(color);
                }
            }
        }
        // === Xử lý ảnh ===
        if (productRequest.getMainImageId() == null || productRequest.getMainImageId().isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Cần chọn ảnh chính")
                    .build();
        }
        if (productRequest.getImageIds() != null) {
            // Xóa ảnh không còn trong danh sách `imageIds`
            List<Image> imagesToRemove = product.getImages().stream()
                    .filter(image -> !productRequest.getImageIds().contains(image.getId()))
                    .collect(Collectors.toList());

            for (Image image : imagesToRemove) {

                imageUploadService.deleteImage(image.getUrl());
                product.getImages().remove(image);
            }
        }

        // Xử lý thêm ảnh màu mới
        if (colorImages != null) {
            for (Map.Entry<String, MultipartFile[]> entry : colorImages.entrySet()) {
                String color = entry.getKey();
                MultipartFile[] files = entry.getValue();
                for (MultipartFile file : files) {
                    try {
                        String imageUrl = imageUploadService.uploadImage(file);
                        Image image = new Image();
                        image.setUrl(imageUrl);
                        image.setColor(color);
                        image.setProduct(product);
                        product.getImages().add(image);
                    } catch (IOException e) {
                        return APIResponse.builder()
                                .statusCode(Code.INTERNAL_SERVER_ERROR.getCode())
                                .message("Failed to upload image")
                                .build();
                    }
                }
            }
        }

        // Cập nhật ảnh chính
        if (productRequest.getMainImageId() != null) {
            Image mainImage = product.getImages().stream()
                    .filter(image -> image.getId().equals(productRequest.getMainImageId()))
                    .findFirst()
                    .orElse(null);
            product.setMainImage(mainImage);
        }

        productRepository.save(product);
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Cập nhật sản phẩm thành công")
                .build();
    }
    @Override
    public String extractColorFromFileName(String fileName, Map<String, String> colorMap) {
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            if (fileName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "unknown";
    }
    @Override
    public MultipartFile[] appendToArray(MultipartFile[] array, MultipartFile file) {
        MultipartFile[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[array.length] = file;
        return newArray;
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
    @Override
    public APIResponse getColorByProductId(String productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Product not found")
                    .build();
        }
        Set<String> colors = product.getVariants().stream()
                .map(variant -> variant.getColor().getColor())
                .collect(Collectors.toSet());
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Colors found")
                .data(colors)
                .build();
    }
    @Override
    public APIResponse getAllImages() {
        List<Image> images = imageRepository.findAll();
        List<ImageResponse> imageResponses = images.stream()
                .map(image -> new ImageResponse(image.getId(), image.getUrl(), image.getColor()))
                .collect(Collectors.toList());
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Images found")
                .data(imageResponses)
                .build();
    }
    @Override
    public PaginatedResponse<ProductResponse> getProductByImgUrl(List<String> imgUrls, Pageable pageable) {
        String url = "https://dbimage.blob.core.windows.net/images/";
        List<String> fullImgUrls = imgUrls.stream()
                .map(imgUrl -> url + imgUrl)
                .collect(Collectors.toList());
        Page<Product> productPage = productRepository.findByImgUrls(fullImgUrls, pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                productResponses,
                productPage.getTotalPages(),
                productPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }



}