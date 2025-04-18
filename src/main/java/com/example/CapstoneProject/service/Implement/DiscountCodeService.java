package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.DiscountCode;
import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.DiscountCodeRepository;
import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.DiscountResponse;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.service.Interface.IDiscountCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountCodeService implements IDiscountCodeService {

    @Autowired
    private DiscountCodeRepository discountCodeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;

    private static final SecureRandom random = new SecureRandom();

    @Override
    public APIResponse createRandomDiscountCode(double discountPercentage, LocalDateTime expiryDate) {
        String code = generateRandomCode();
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode(code);
        discountCode.setDiscountPercentage(discountPercentage);
        discountCode.setStatus("AVAILABLE");
        discountCode.setExpiryDate(expiryDate);
        discountCodeRepository.save(discountCode);
        return new APIResponse(200, "Discount code created successfully", true);
    }
    private String generateRandomCode() {
        String CODE;
        do {
            byte[] RANDOM_BYTES = new byte[8];
            random.nextBytes(RANDOM_BYTES);
            CODE = Base64.getUrlEncoder().withoutPadding().encodeToString(RANDOM_BYTES);
        } while (discountCodeRepository.findByCode(CODE).isPresent());
        return CODE;
    }
    @Override
    public APIResponse getAllDiscountCodes() {
        List<DiscountCode> discountCodes = discountCodeRepository.findAll();
        List<DiscountResponse> discountResponses = discountCodes.stream()
                .map(discountCode -> DiscountResponse.builder()
                        .code(discountCode.getCode())
                        .discountPercentage(discountCode.getDiscountPercentage())
                        .expiryDate(discountCode.getExpiryDate())
                        .status(discountCode.getStatus())
                        .build())
                .toList();
        return new APIResponse(200, "Lấy danh sách mã giảm giá thành công", discountResponses);
    }
    @Override
    public APIResponse getDiscountCodesByUser(String token) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return new APIResponse(404, "User not found", null);
        }
        List<DiscountCode> discountCodes = discountCodeRepository.findByUser(user.get());
        List<DiscountResponse> discountResponses = discountCodes.stream()
                .map(discountCode -> DiscountResponse.builder()
                        .code(discountCode.getCode())
                        .discountPercentage(discountCode.getDiscountPercentage())
                        .expiryDate(discountCode.getExpiryDate())
                        .status(discountCode.getStatus())
                        .build())
                .toList();
        return new APIResponse(200, "Lấy danh sách mã giảm giá thành công", discountResponses);
    }
    @Override
    public APIResponse applyDiscountCodeToUser(String discountCode, String userId) {
        Optional<DiscountCode> discountCodeOpt = discountCodeRepository.findByCode(discountCode);
        if (discountCodeOpt.isEmpty()) {
            return new APIResponse(404, "Discount code not found", null);
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return new APIResponse(404, "User not found", null);
        }

        DiscountCode code = discountCodeOpt.get();
        code.setUser(userOpt.get());
        code.setStatus("ASSIGNED");
        discountCodeRepository.save(code);

        return new APIResponse(200, "Thêm mã giảm giá thành công", null);
    }
    @Override
    public APIResponse applyDiscountCode(String discountCode, String token) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return new APIResponse(404, "User not found", null);
        }
        return applyDiscountCodeToUser(discountCode, user.get().getId());
    }

}