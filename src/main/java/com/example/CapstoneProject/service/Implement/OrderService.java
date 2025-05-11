package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.request.AddressRequest;
import com.example.CapstoneProject.request.PaymentRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.*;
import com.example.CapstoneProject.repository.*;
import com.example.CapstoneProject.response.*;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.service.Interface.IOrderService;
import com.example.CapstoneProject.ws.OrderNotificationWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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

    @Autowired
    private OrderNotificationWebSocketHandler orderNotificationWebSocketHandler;
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
    public APIResponse createOrderNow(PaymentRequest request) throws IOException {
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
                .findByProductIdAndSizeNameIdAndColorName(request.getProductId(), request.getSize(), request.getColor());

        if (productVariantOpt.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Product variant not foundd")
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

        AddressRequest addressDTO = request.getAddress();
        if (addressDTO != null) {
            String fullAddress = String.format("%s, %s, %s",
                    addressDTO.getStreet(),
                    addressDTO.getDistrict(),
                    addressDTO.getCity());
            order.setDeliveryAddress(fullAddress);
        } else {
            order.setDeliveryAddress(user.get().getAddress());
        }

        order.setDeliveryPhone(request.getDeliveryPhone());
        order.setTotalAmount(Double.valueOf(request.getAmount()));
        if (request.getPaymentMethod().equals("ZALOPAY")) {
            order.setStatus("PAID");
        } else {
            order.setStatus("PENDING");
        }
        orderRepository.save(order);

        // Tạo chi tiết đơn hàng
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(productVariant.getProduct());
        orderDetail.setQuantity(request.getQuantity());
        orderDetail.setTotalPrice(Double.valueOf(request.getAmount()));
        orderDetail.setSize(productVariant.getSize());
        orderDetail.setColor(productVariant.getColor());
        orderDetailRepository.save(orderDetail);

        // Gửi thông báo đến WebSocket
        orderNotificationWebSocketHandler.sendOrderNotification(order.getOrderCode());

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Đặt hàng thành công")
                .build();
    }

    @Override
    public APIResponse createOrderFromCart(PaymentRequest request) throws IOException {
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
        AddressRequest addressDTO = request.getAddress();
        if (addressDTO != null) {
            String fullAddress = String.format("%s, %s, %s",
                    addressDTO.getStreet(),
                    addressDTO.getDistrict(),
                    addressDTO.getCity());
            order.setDeliveryAddress(fullAddress);
        } else {
            order.setDeliveryAddress(user.get().getAddress());
        }

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

        // Send notification to WebSocket
        orderNotificationWebSocketHandler.sendOrderNotification(order.getOrderCode());

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Order created successfully from cart")
                .build();
    }
    @Override
    public APIResponse cancelOrder(String token, String OrderCode) {
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
        // Retrieve the order by its code
        Optional<Order> orderOpt = orderRepository.findByOrderCode(OrderCode);
        if (orderOpt.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Order not found")
                    .build();
        }

        Order order = orderOpt.get();

        // Check if the order is already canceled
        if ("CANCELED".equals(order.getStatus())) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Order is already canceled")
                    .build();
        }

        // Update stock quantity for each product variant in the order
        List<OrderDetail> orderDetails = order.getOrderDetails();
        for (OrderDetail orderDetail : orderDetails) {
            ProductVariant productVariant = productVariantRepository.findByProductIdAndSizeIdAndColorId(
                    orderDetail.getProduct().getId(),
                    orderDetail.getSize().getSizeId(),
                    orderDetail.getColor().getColorId()
            ).orElse(null);

            if (productVariant != null) {
                productVariant.setQuantity(productVariant.getQuantity() + orderDetail.getQuantity());
                productVariantRepository.save(productVariant);
            }
        }

        // Mark the order as canceled
        order.setStatus("CANCELED");
        orderRepository.save(order);

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Huỷ đơn hàng thành công")
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
                        .isFeedback(order.getFeedback())
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
        Pageable sortedByCreatedAtDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        Page<Order> orderPage = orderRepository.findAll(sortedByCreatedAtDesc);
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

    @Override
    public APIResponse updateOrderStatus(String orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findByOrderCode(orderId);
        if (orderOpt.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Order not found")
                    .build();
        }

        Order order = orderOpt.get();
        order.setStatus(status);
        orderRepository.save(order);

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Cập nhật trạng thái đơn hàng thành công")
                .build();
    }
    @Override
    public APIResponse getOrdersWithinLastHour() {
        // Lấy các đơn trong vòng 1 giờ gần nhất
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Order> recentOrders = orderRepository.findByCreatedAtAfter(oneHourAgo);

        List<NewOrderResponse> newOrderResponses = recentOrders.stream()
                .map(order -> NewOrderResponse.builder()
                        .id(order.getId().toString())
                        .imageUrl(order.getOrderDetails().get(0).getProduct().getMainImage().getUrl())
                        .productName(order.getOrderDetails().get(0).getProduct().getProductName())
                        .productId(order.getOrderDetails().get(0).getProduct().getId().toString())
                        .orderCode(order.getOrderCode())
                        .userName(order.getUser().getFullName())
                        // orderDate giờ là String
                        .orderDate(formatTime(order.getCreatedAt()))
                        .build()
                )
                .collect(Collectors.toList());

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Orders retrieved successfully")
                .data(newOrderResponses)
                .build();
    }

    /**
     * Trả về chuỗi “x phút trước”, “x giờ trước” hoặc “x ngày trước”
     */
    public static String formatTime(LocalDateTime givenTime) {
        ZoneId hoChiMinhZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime now = LocalDateTime.now(hoChiMinhZone);
        long minutesAgo = Duration.between(givenTime, now).toMinutes();
        long hoursAgo = Duration.between(givenTime, now).toHours();
        long daysAgo = Duration.between(givenTime, now).toDays();

        if (minutesAgo < 60) {
            return minutesAgo + " phút trước";
        } else if (hoursAgo < 24) {
            return hoursAgo + " giờ trước";
        } else {
            return daysAgo + " ngày trước";
        }
    }
    @Override
    public Integer getTotalSoldByProductId(String productId) {
        // Lấy tất cả các đơn hàng
        List<Order> orders = orderRepository.findAll();

        // Tính tổng số lượng đã bán cho sản phẩm cụ thể
        int totalSold = orders.stream()
                .flatMap(order -> order.getOrderDetails().stream())
                .filter(orderDetail -> orderDetail.getProduct().getId().equals(productId))
                .mapToInt(OrderDetail::getQuantity)
                .sum();
        return totalSold;
    }

    @Override
    public APIResponse getOrderStatistics() {
        // Total orders
        long totalOrders = orderRepository.count();

        // Total revenue
        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // Orders by status
        Map<String, Long> ordersByStatus = orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));

        // Orders by date (grouped by day)
        Map<String, Long> ordersByDate = orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(order -> order.getCreatedAt().toLocalDate().toString(), Collectors.counting()));

        // Build response
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalOrders", totalOrders);
        statistics.put("totalRevenue", totalRevenue);
        statistics.put("ordersByStatus", ordersByStatus);
        statistics.put("ordersByDate", ordersByDate);

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Order statistics retrieved successfully")
                .data(statistics)
                .build();
    }



}
