package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.request.PaymentRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.*;
import com.example.CapstoneProject.repository.*;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.OrderDetailResponse;
import com.example.CapstoneProject.response.OrderHistoryResponse;
import com.example.CapstoneProject.response.PaginatedResponse;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.service.Interface.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private CollectionRepository collectionRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Override
    public String generateUniqueOrderCode() {
        Random random = new Random();
        String orderCode;
        do {
            orderCode = String.format("%06d", random.nextInt(900000) + 100000);
        } while (orderRepository.existsByOrderCode(orderCode));
        return orderCode;
    }

    @Override
    public APIResponse createOrderNow(PaymentRequest request) {
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

        // Kiểm tra sản phẩm có tồn tại không
        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if (productOpt.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Product not found")
                    .build();
        }

        // Kiểm tra ProductVariant (size, color)
        Optional<ProductVariant> productVariantOpt = productVariantRepository
                .findByProductIdAndSizeIdAndColorId(request.getProductId(), request.getSize(), request.getColor());

        if (productVariantOpt.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Product variant not found")
                    .build();
        }

        ProductVariant productVariant = productVariantOpt.get();

        // Kiểm tra số lượng sản phẩm có đủ không
        if (productVariant.getQuantity() < request.getQuantity()) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Not enough quantity in stock")
                    .build();
        }

        // Cập nhật số lượng sản phẩm trong kho
        productVariant.setQuantity(productVariant.getQuantity() - request.getQuantity());
        productVariantRepository.save(productVariant);

        // Tạo đơn hàng
        Order order = new Order();
        order.setUser(user.get());
        order.setOrderCode(generateUniqueOrderCode());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryPhone(request.getDeliveryPhone());
        order.setTotalAmount(Double.valueOf(request.getAmount()));
        order.setStatus("PENDING");
        orderRepository.save(order);

        // Tạo chi tiết đơn hàng
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(productOpt.get());
        orderDetail.setQuantity(request.getQuantity());
        orderDetail.setTotalPrice((double) (productVariant.getPrice() * request.getQuantity()));
        orderDetail.setSize(sizeRepository.findById(request.getSize()).get());
        orderDetail.setColor(colorRepository.findById(request.getColor()).get());
        orderDetailRepository.save(orderDetail);

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Order created successfully")
                .build();
    }

    @Override
    public APIResponse createOrderFromCart(PaymentRequest request) {
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

        // Retrieve cart items
        List<Cart> cartItems = cartRepository.findByUserIdAndIdIn(user.get().getId(), request.getCartIds());
        if (cartItems.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Cart is empty or invalid cart IDs")
                    .build();
        }


        // Create order
        Order order = new Order();
        order.setUser(user.get());
        order.setOrderCode(generateUniqueOrderCode());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryPhone(request.getDeliveryPhone());
        if (request.getPaymentMethod().equals("ZALOPAY")) {
            order.setStatus("PAID");
        } else {
            order.setStatus("PENDING");
        }
        order.setTotalAmount(Double.valueOf(request.getAmount()));
        orderRepository.save(order);

        for (Cart cartItem : cartItems) {
            // Check if product variant exists
            Optional<ProductVariant> productVariantOpt = productVariantRepository
                    .findByProductIdAndSizeIdAndColorId(
                            cartItem.getProductVariant().getProduct().getId(),
                            cartItem.getProductVariant().getSize().getSizeId(),
                            cartItem.getProductVariant().getColor().getColorId()
                    );

            if (productVariantOpt.isEmpty()) {
                return APIResponse.builder()
                        .statusCode(Code.NOT_FOUND.getCode())
                        .message("Product variant not found for cart item")
                        .build();
            }

            ProductVariant productVariant = productVariantOpt.get();

            // Check if enough quantity is available
            if (productVariant.getQuantity() < cartItem.getQuantity()) {
                return APIResponse.builder()
                        .statusCode(Code.BAD_REQUEST.getCode())
                        .message("Not enough quantity in stock for cart item")
                        .build();
            }

            // Update product variant quantity
            productVariant.setQuantity(productVariant.getQuantity() - cartItem.getQuantity());
            productVariantRepository.save(productVariant);

            // Create order detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(productVariant.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setTotalPrice(Double.valueOf(request.getAmount()));
            orderDetail.setSize(productVariant.getSize());
            orderDetail.setColor(productVariant.getColor());
            orderDetailRepository.save(orderDetail);
        }
        // Clear cart
        cartRepository.deleteAll(cartItems);

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Order created successfully from cart")
                .build();
    }

    @Override
    public APIResponse getHistoryOrder(String token) {
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
        List<Order> orders = orderRepository.findByUser(user.get());
        Optional<User> finalUser = user;
        List<OrderHistoryResponse> orderHistoryResponses = orders.stream()
                .map(order -> OrderHistoryResponse.builder()
                        .orderCode(order.getOrderCode())
                        .userName(finalUser.get().getFullName())
                        .orderDate(order.getCreatedAt())
                        .status(order.getStatus())
                        .deliveryAddress(Optional.ofNullable(order.getDeliveryAddress()).orElse("N/A"))
                        .deliveryPhone(Optional.ofNullable(order.getDeliveryPhone()).orElse("N/A"))
                        .totalAmount(order.getTotalAmount())
                        .orderDetails(order.getOrderDetails().stream()
                                .map(orderDetail -> OrderDetailResponse.builder()
                                        .productName(orderDetail.getProduct().getProductName())
                                        .imgUrl(Optional.ofNullable(orderDetail.getProduct().getMainImage()).map(Image::getUrl).orElse("N/A"))
                                        .quantity(orderDetail.getQuantity())
                                        .totalPrice(orderDetail.getTotalPrice())
                                        .size(Optional.ofNullable(orderDetail.getSize()).map(Size::getName).orElse("N/A"))
                                        .color(Optional.ofNullable(orderDetail.getColor()).map(Color::getColor).orElse("N/A"))
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Order history retrieved successfully")
                .data(orderHistoryResponses)
                .build();
    }
    @Override
    public APIResponse getAllOrder(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<OrderHistoryResponse> orderHistoryResponses = orderPage.getContent().stream()
                .map(order -> OrderHistoryResponse.builder()
                        .orderCode(order.getOrderCode())
                        .userName(order.getUser().getFullName())
                        .orderDate(order.getCreatedAt())
                        .status(order.getStatus())
                        .deliveryAddress(Optional.ofNullable(order.getDeliveryAddress()).orElse("N/A"))
                        .deliveryPhone(Optional.ofNullable(order.getDeliveryPhone()).orElse("N/A"))
                        .totalAmount(order.getTotalAmount())
                        .orderDetails(order.getOrderDetails().stream()
                                .map(orderDetail -> OrderDetailResponse.builder()
                                        .productName(orderDetail.getProduct().getProductName())
                                        .imgUrl(Optional.ofNullable(orderDetail.getProduct().getMainImage()).map(Image::getUrl).orElse("N/A"))
                                        .quantity(orderDetail.getQuantity())
                                        .totalPrice(orderDetail.getTotalPrice())
                                        .size(Optional.ofNullable(orderDetail.getSize()).map(Size::getName).orElse("N/A"))
                                        .color(Optional.ofNullable(orderDetail.getColor()).map(Color::getColor).orElse("N/A"))
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        PaginatedResponse<OrderHistoryResponse> paginatedResponse = new PaginatedResponse<>(
                orderHistoryResponses,
                orderPage.getTotalPages(),
                orderPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Order history retrieved successfully")
                .data(paginatedResponse)
                .build();
    }

}
