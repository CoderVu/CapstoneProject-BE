package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.request.OrderRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.*;
import com.example.CapstoneProject.repository.*;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.service.Interface.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

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
    private JwtUtils jwtUtils;
    public String generateUniqueOrderCode() {
        Random random = new Random();
        String orderCode;
        do {
            orderCode = String.format("%06d", random.nextInt(900000) + 100000);
        } while (orderRepository.existsByOrderCode(orderCode));
        return orderCode;
    }

    @Override
    public APIResponse createOrderNow(OrderRequest request) {
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
        orderDetail.setStatus("PENDING");
        orderDetailRepository.save(orderDetail);

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Order created successfully")
                .build();
    }

}
