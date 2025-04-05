package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.response.CartResponse;
import com.example.CapstoneProject.response.PaginatedResponse;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.model.*;
import com.example.CapstoneProject.repository.*;
import com.example.CapstoneProject.request.CartRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService implements ICartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public APIResponse addToCart(CartRequest request) {
        String identifier = jwtUtils.getUserFromToken(request.getToken());
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("User not found")
                    .build();
        }
        Product product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null) {
            return new APIResponse(404, "Không tìm thấy sản phẩm", false);
        }
        Size size = sizeRepository.findByName(request.getSize());
        if (size == null) {
            return new APIResponse(404, "Không tìm thấy size", false);
        }
        System.out.println("Size: " + size.getSizeId());
        Color color = colorRepository.findByColor(request.getColor());
        if (color == null) {
            return new APIResponse(404, "Không tìm thấy màu", false);
        }
        System.out.println("Color: " + color.getColorId());
        ProductVariant productVariant = productVariantRepository.findByProductAndSizeAndColor(product.getId(), size.getSizeId(), color.getColorId());

        if (productVariant == null) {
            return new APIResponse(404, "Không tìm thấy biến thể sản phẩm", false);
        }
        if (productVariant.getQuantity() < request.getQuantity()) {
            return new APIResponse(404, "Số lượng sản phẩm không đủ", false);
        }
        User userEntity = user.get();
        Cart cart = cartRepository.findByUserAndProductVariant(userEntity, productVariant);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(userEntity);
            cart.setProductVariant(productVariant);
            cart.setQuantity(request.getQuantity());
            cart.setUnitPrice(Double.valueOf(product.getPrice()));
            cart.setTotalPrice(Double.valueOf(product.getPrice() * request.getQuantity()));
            cart.setStatus("PENDING");
        } else {
            cart.setQuantity(cart.getQuantity() + request.getQuantity());
        }
        cartRepository.save(cart);
        return new APIResponse(200, "Thêm vào giỏ hàng thành công", true);
    }
    @Override
    public APIResponse updateCart(String token, String cartId, Integer quantity, String color, String size) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("User not found")
                    .build();
        }
        Optional<Cart> cart = cartRepository.findById(cartId);
        if (cart.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Không tìm thấy giỏ hàng")
                    .build();
        }

        Cart cartEntity = cart.get();

        if (quantity != null) {
            cartEntity.setQuantity(quantity);
            cartEntity.setTotalPrice(cartEntity.getUnitPrice() * quantity);
        }

        if (color != null) {
            Color colorEntity = colorRepository.findByColor(color);
            if (colorEntity == null) {
                return new APIResponse(404, "Màu không tìm thấy", false);
            }
            cartEntity.getProductVariant().setColor(colorEntity);
        }

        if (size != null) {
            Size sizeEntity = sizeRepository.findByName(size);
            if (sizeEntity == null) {
                return new APIResponse(404, "Kích thước không tìm thấy", false);
            }
            cartEntity.getProductVariant().setSize(sizeEntity);
        }

        cartRepository.save(cartEntity);
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Cập nhật giỏ hàng thành công")
                .build();
    }
    @Override
    public APIResponse deleteCart(String token, String cartId) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("User not found")
                    .build();
        }
        Optional<Cart> cart = cartRepository.findById(cartId);
        if (cart.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Cart not found")
                    .build();
        }
        cartRepository.delete(cart.get());
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Delete cart successfully")
                .build();
    }

    @Override
    public PaginatedResponse<CartResponse> getCart(String token, int page, int size) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }

        if (user.isEmpty()) {
            return new PaginatedResponse<>(
                    Collections.emptyList(), 0, 0, page, size
            );
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Cart> carts = cartRepository.findByUser(user.get(), pageable);
        List<CartResponse> cartResponses = carts.getContent().stream()
                .map(cart -> {
                    Product product = cart.getProductVariant().getProduct();
                    return CartResponse.builder()
                            .id(cart.getId())
                            .userId(cart.getUser().getId())
                            .userName(cart.getUser().getFullName())
                            .avatar(cart.getUser() != null ? cart.getUser().getAvatar() : null)
                            .productId(product != null ? product.getId() : null)
                            .productName(product != null ? product.getProductName() : null)
                            .image(product != null ? String.valueOf(product.getMainImage().getUrl()) : null)
                            .categoryName(product != null && product.getCategory() != null ? product.getCategory().getName() : null)
                            .brand(product != null && product.getBrand() != null ? product.getBrand().getName() : null)
                            .color(cart.getProductVariant().getColor() != null ? cart.getProductVariant().getColor().getColor() : null)
                            .statusColor(cart.getProductVariant().getColor() != null ? cart.getProductVariant().getColor().getStatus() : null)
                            .size(cart.getProductVariant().getSize() != null ? cart.getProductVariant().getSize().getName() : null)
                            .statusSize(cart.getProductVariant().getSize() != null ? cart.getProductVariant().getSize().getStatusSize() : null)
                            .unitPrice(cart.getProductVariant() != null ? cart.getProductVariant().getPrice() : null)
                            .quantity(cart.getQuantity())
                            .totalPrice(cart.getTotalPrice())
                            .discountPrice(product.getDiscountPrice() != null ? product.getDiscountPrice() : null)
                            .status(cart.getStatus())
                            .build();
                })
                .collect(Collectors.toList());

        return PaginatedResponse.<CartResponse>builder()
                .Response(cartResponses)
                .totalPages(carts.getTotalPages())
                .totalElements(carts.getTotalElements())
                .size(carts.getSize())
                .build();

    }

}