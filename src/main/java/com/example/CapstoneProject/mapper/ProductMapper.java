package com.example.CapstoneProject.mapper;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.Color;
import com.example.CapstoneProject.model.Size;
import com.example.CapstoneProject.response.*;
import com.example.CapstoneProject.Request.ProductRequest;
import com.example.CapstoneProject.Request.VariantRequest;
import com.example.CapstoneProject.model.Product;
import com.example.CapstoneProject.model.ProductVariant;
import com.example.CapstoneProject.repository.BrandRepository;
import com.example.CapstoneProject.repository.CategoryRepository;
import com.example.CapstoneProject.repository.ColorRepository;
import com.example.CapstoneProject.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    public Product toProduct(ProductRequest request) {
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(brandRepository.findByName(request.getBrandName()));
        product.setCategory(categoryRepository.findByName(request.getCategoryName()));
        product.setNewProduct(request.getNewProduct());
        return product;
    }
    public APIResponse toProductVariant(VariantRequest variantRequest, Product product, boolean isUpdate) {
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        Size size = sizeRepository.findByName(variantRequest.getSizeName());
        if (size == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Size not found")
                    .build();
        }
        variant.setSize(size);
        Color color = colorRepository.findByColor(variantRequest.getColorName());
        if (color == null) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Color not found")
                    .build();
        }
        variant.setColor(color);
        if (variantRequest.getQuantity() < 0) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Quantity must be greater than 0")
                    .build();
        }
        variant.setQuantity(variantRequest.getQuantity());

        if (variantRequest.getPrice() < 0) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Price must be greater than 0")
                    .build();
        }
        variant.setPrice(variantRequest.getPrice());

        if (!isUpdate && product.getVariants().stream()
                .anyMatch(v -> v.getSize().getName().equals(variant.getSize().getName())
                        && v.getColor().getColor().equals(variant.getColor().getColor()))) {
            return APIResponse.builder()
                    .statusCode(Code.CONFLICT.getCode())
                    .message("Variant already exists")
                    .build();
        }

        if (isUpdate) {
            ProductVariant oldVariant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(variantRequest.getId()))
                    .findFirst()
                    .orElse(null);
            if (oldVariant == null) {
                return APIResponse.builder()
                        .statusCode(Code.NOT_FOUND.getCode())
                        .message("Variant not found")
                        .build();
            }
            oldVariant.setSize(size);
            oldVariant.setColor(color);
            oldVariant.setQuantity(variantRequest.getQuantity());
            oldVariant.setPrice(variantRequest.getPrice());
            return APIResponse.builder()
                    .statusCode(Code.OK.getCode())
                    .message("Variant updated successfully")
                    .build();
        }

        product.getVariants().add(variant);
        return APIResponse.builder()
                .statusCode(Code.CREATED.getCode())
                .message("Variant added successfully")
                .build();
    }
    public ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .brandName(product.getBrand().getName())
                .categoryName(product.getCategory().getName())
                .collections(product.getCollections().stream()
                        .map(collection -> CollectionResponse.builder()
                                .id(collection.getId())
                                .name(collection.getName())
                                .build())
                        .collect(Collectors.toList()))

                .newProduct(product.getNewProduct())
                .images(product.getImages().stream()
                        .map(image -> new ImageResponse(image.getId(), image.getUrl()))
                        .collect(Collectors.toList()))
                .mainImage(product.getMainImage() != null
                        ? new ImageResponse(product.getMainImage().getId(), product.getMainImage().getUrl())
                        : null)
                .variants(product.getVariants().stream()
                        .map(variant -> VariantResponse.builder()
                                .sizeName(variant.getSize().getName())
                                .color(variant.getColor().getColor())
                                .quantity(variant.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

}