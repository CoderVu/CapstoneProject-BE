package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.OTP;
import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.OTPRepository;
import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.IOTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPService implements IOTPService {
    @Autowired
    private OTPRepository otpRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public OTP createOTP(String email, String userId) {
        OTP otp = new OTP();
        otp.setEmail(email);
        otp.setUserId(userId);
        otp.setOtp(generateRandomOTP());
        return otpRepository.save(otp);
    }

    private String generateRandomOTP() {
        int randomPin = (int)(Math.random()*900000) + 100000; // 6 chữ số
        return String.valueOf(randomPin);
    }
    @Override
    public APIResponse verifyOTP(String email, String inputOtp) {
        OTP otp = otpRepository.findTopByEmailAndOtpOrderByCreatedAtDesc(email, inputOtp).orElse(null);
        if (otp == null) {
//            System.out.println("OTP not found or expired for email: " + email);
            return new APIResponse(Code.BAD_GATEWAY.getCode(), "OTP không hợp lệ hoặc đã hết hạn", null);
        }
        if (otp.isVerified()) {
//            System.out.println("OTP already verified for email: " + email);
            return new APIResponse(Code.BAD_GATEWAY.getCode(), "OTP đã được xác thực trước đó", null);
        }
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
//            System.out.println("OTP expired for email: " + email);
            return new APIResponse(Code.BAD_GATEWAY.getCode(), "OTP đã hết hạn", null);
        }

        otp.setVerified(true);
        // Tim user by email
        Optional<User>  userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(Boolean.TRUE);
            userRepository.save(user);
        } else {
            return new APIResponse(Code.BAD_GATEWAY.getCode(), "Người dùng không tồn tại", null);
        }
        // Update user status to verified

        otpRepository.save(otp);
        System.out.println("OTP successfully verified for email: " + email);
        return new APIResponse(Code.OK.getCode(), "Xác thực OTP thành công", null);
    }
    @Override
    public APIResponse verifyOTPForgetPassword(String email, String inputOtp) {
        OTP otp = otpRepository.findTopByEmailAndOtpOrderByCreatedAtDesc(email, inputOtp).orElse(null);
        if (otp == null) {
            return new APIResponse(Code.BAD_GATEWAY.getCode(), "OTP không hợp lệ hoặc đã hết hạn", null);
        }
        if (otp.isVerified()) {
            return new APIResponse(Code.BAD_GATEWAY.getCode(), "OTP đã được xác thực trước đó", null);
        }
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            return new APIResponse(Code.BAD_GATEWAY.getCode(), "OTP đã hết hạn", null);
        }

        otp.setVerified(true);
        otpRepository.save(otp);
        return new APIResponse(Code.OK.getCode(), "Xác thực OTP thành công", null);
    }


}
