package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.*;
import com.example.CapstoneProject.repository.*;
import com.example.CapstoneProject.request.RateRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.service.ImageUploadService;
import com.example.CapstoneProject.service.Interface.IRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class RateService implements IRateService {
    @Autowired
    private RateRepository rateRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRateRepository imageRateRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ImageUploadService imageUploadService;
    @Autowired
    private JwtUtils jwtUtils;


    @Override
    public APIResponse rateProduct(RateRequest request) {
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
        Optional<Order> order = orderRepository.findById(request.getOrderId());
        if (order.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Order not found")
                    .build();
        }
        if (order.get().getFeedback()) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Feedback already provided for this order")
                    .build();
        }
        order.get().setFeedback(true);

        for (OrderDetail orderDetail : order.get().getOrderDetails()) {
            Product product = orderDetail.getProduct();
            Rate rate = new Rate();
            rate.setUserId(user.get().getId());
            rate.setProduct(product);
            rate.setRate(request.getRate());
            rate.setComment(request.getComment());
            rateRepository.save(rate);
            List<MultipartFile> images = request.getImages();
            if (images != null) {
                for (MultipartFile image : images) {
                    try {
                        String imageUrl = imageUploadService.uploadImage(image);
                        ImageRate imageRate = new ImageRate();
                        imageRate.setRate(rate);
                        imageRate.setUrl(imageUrl);
                        rate.getImageRatings().add(imageRate);
                    } catch (IOException e) {
                        return APIResponse.builder()
                                .statusCode(Code.INTERNAL_SERVER_ERROR.getCode())
                                .message("Image upload failed")
                                .build();
                    }
                }
            }
            rateRepository.save(rate);
        }
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Products rated successfully")
                .build();
    }
}