package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.OTP;
import com.example.CapstoneProject.request.RegisterRequest;
import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.Role;
import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.RoleRepository;
import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.response.RoleResponse;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.security.user.ShopUserDetails;
import com.example.CapstoneProject.service.Interface.IAuthService;
import com.example.CapstoneProject.service.Interface.IEmailService;
import com.example.CapstoneProject.service.Interface.IOTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private IOTPService otpService;

    @Override
    public APIResponse authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByPhoneNumber(username);
        if (user.isEmpty()) {
            user = userRepository.findByEmail(username);
        }
        if (user.isEmpty()) {
            return APIResponse.builder().statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("Tài khoản không tồn tại")
                    .build();
        }
        if (user.get().getPassword() == null) {
            return APIResponse.builder().statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("Tài khoản này đã được đăng nhập bằng tài khoản Google. Vui lòng sử dụng tài khoản Google để đăng nhập")
                    .build();
        }
        if (user.get().getEnabled().equals(false)) {
            return APIResponse.builder().statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("Tài khoản chưa được xác thực. Vui lòng kiểm tra email để xác thực tài khoản")
                    .build();
        }

        try {
            // For normal authentication, we need to validate the password manually
            if (!passwordEncoder.matches(password, user.get().getPassword())) {
                return APIResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Thông tin đăng nhập không chính xác")
                        .build();
            }

            ShopUserDetails userDetails = ShopUserDetails.buildUserDetails(user.get());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtTokenForUser(authentication);

            JwtResponse jwtResponse = JwtResponse.builder()
                    .id(user.get().getId().toString())
                    .email(user.get().getEmail())
                    .fullName(user.get().getFullName())
                    .phoneNumber(user.get().getPhoneNumber())
                    .address(user.get().getAddress())
                    .avatar(user.get().getAvatar())
                    .token(jwt)
                    .methodLogin(user.get().getMethodLogin())
                    .role(RoleResponse.builder()
                            .id(user.get().getRole().getId())
                            .name(user.get().getRole().getName())
                            .build())
                    .build();

            return APIResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Đang nhập thành công")
                    .data(jwtResponse)
                    .build();
        } catch (BadCredentialsException e) {
            return APIResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Thông tin đăng nhập không chính xác")
                    .build();
        }
    }

    @Override
    public APIResponse registerUser(RegisterRequest request) {
        // Validate phone number
        String phoneRegex = "^[0-9]{10}$";
        if (!request.getPhoneNumber().matches(phoneRegex)) {
            return APIResponse.error(Code.BAD_REQUEST.getCode(), "Nhập số điện thoại không hợp lệ. Vui lòng nhập số điện thoại 10 chữ số.");
        }

        // Validate email
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; // Standard email format
        if (!request.getEmail().matches(emailRegex)) {
            return APIResponse.error(Code.BAD_REQUEST.getCode(), "Nhập email không hợp lệ. Vui lòng nhập email theo định dạng chuẩn.");
        }

        // Check if phone number or email already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return APIResponse.error(Code.CONFLICT.getCode(), request.getPhoneNumber() + " đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return APIResponse.error(Code.CONFLICT.getCode(), request.getEmail() + " đã tồn tại");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setMethodLogin("NORMAL");
        Optional<Role> optionalRole = Optional.ofNullable(roleRepository.findByName("ROLE_USER"));
        if (optionalRole.isEmpty()) {
            return APIResponse.error(Code.NOT_FOUND.getCode(), "ROLE_USER not found");
        }
        user.setRole(optionalRole.get());
        user.setEnabled(Boolean.FALSE);
        user.setIsDeleted(Boolean.FALSE);
        userRepository.save(user);

        OTP otp = otpService.createOTP(request.getEmail(), user.getId());
        String subject = "Verify Your Account";
        String content = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".container { background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }" +
                ".header { text-align: center; margin-bottom: 30px; }" +
                ".otp-code { background-color: #f5f5f5; padding: 15px; text-align: center; font-size: 32px; letter-spacing: 4px; font-weight: bold; color: #2c3e50; border-radius: 4px; margin: 20px 0; }" +
                ".footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #888; }" +
                ".warning { color: #e74c3c; font-size: 12px; margin-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2 style='color: #2c3e50; margin-bottom: 10px;'>Welcome to Vu Nguyen Coder</h2>" +
                "<p>Thank you for creating an account. To complete your registration and verify your email address, please use the following verification code:</p>" +
                "</div>" +
                "<div class='otp-code'>" + otp.getOtp() + "</div>" +
                "<p style='text-align: center;'><strong>This code will expire in 5 minutes</strong></p>" +
                "<div class='warning'>" +
                "<p>If you didn't create an account with us, please ignore this email or contact support if you have concerns about your account security.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>This is an automated message, please do not reply to this email.</p>" +
                "<p>© " + LocalDateTime.now().getYear() + " Vu Nguyen Coder. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        emailService.sendEmail(request.getEmail(), subject, content);
        return APIResponse.success(Code.CREATED.getCode(), "Vui lòng kiểm tra email để xác thực tài khoản", null);
    }

    @Override
    public APIResponse registerStaff(RegisterRequest request) {
        // Validate phone number
        String phoneRegex = "^[0-9]{10}$";
        if (!request.getPhoneNumber().matches(phoneRegex)) {
            return APIResponse.error(Code.BAD_REQUEST.getCode(), "Nhập số điện thoại không hợp lệ. Vui lòng nhập số điện thoại 10 chữ số.");
        }

        // Validate email
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; // Standard email format
        if (!request.getEmail().matches(emailRegex)) {
            return APIResponse.error(Code.BAD_REQUEST.getCode(), "Nhập email không hợp lệ. Vui lòng nhập email theo định dạng chuẩn.");
        }

        // Check if phone number or email already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return APIResponse.error(Code.CONFLICT.getCode(), request.getPhoneNumber() + " đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return APIResponse.error(Code.CONFLICT.getCode(), request.getEmail() + " đã tồn tại");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setMethodLogin("NORMAL");

        Optional<Role> optionalRole = Optional.ofNullable(roleRepository.findByName("ROLE_STAFF"));
        if (optionalRole.isEmpty()) {
            return APIResponse.error(Code.NOT_FOUND.getCode(), "ROLE_STAFF not found");
        }

        user.setRole(optionalRole.get());
        user.setEnabled(Boolean.TRUE);
        user.setIsDeleted(Boolean.FALSE);

        userRepository.save(user);

        return APIResponse.success(Code.CREATED.getCode(), "Đăng ký nhân viên thành công", null);
    }
    @Override
    public APIResponse forgetPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return APIResponse.error(Code.NOT_FOUND.getCode(), "User not found");
        }
        OTP otp = otpService.createOTP(email, user.get().getId());
        String subject = "Reset Your Password";
        String content = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".container { background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }" +
                ".header { text-align: center; margin-bottom: 30px; }" +
                ".otp-code { background-color: #f5f5f5; padding: 15px; text-align: center; font-size: 32px; letter-spacing: 4px; font-weight: bold; color: #2c3e50; border-radius: 4px; margin: 20px 0; }" +
                ".footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #888; }" +
                ".warning { color: #e74c3c; font-size: 12px; margin-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2 style='color: #2c3e50; margin-bottom: 10px;'>Password Reset Request</h2>" +
                "<p>We received a request to reset your password. To proceed with the password reset, please use the following verification code:</p>" +
                "</div>" +
                "<div class='otp-code'>" + otp.getOtp() + "</div>" +
                "<p style='text-align: center;'><strong>This code will expire in 5 minutes</strong></p>" +
                "<div class='warning'>" +
                "<p>If you didn't request a password reset, please ignore this email or contact support if you have concerns about your account security.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>This is an automated message, please do not reply to this email.</p>" +
                "<p>© " + LocalDateTime.now().getYear() + " Vu Nguyen Coder. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        emailService.sendEmail(email, subject, content);
        return APIResponse.success(Code.OK.getCode(), "Vui lòng kiểm tra email để đặt lại mật khẩu", null);
    }
    @Override
    public APIResponse verifyForgetPassword(String email, String inputOtp) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return APIResponse.error(Code.NOT_FOUND.getCode(), "Không tìm thấy người dùng với email: " + email);
        }
        APIResponse otpResult = otpService.verifyOTPForgetPassword(email, inputOtp);
        if (otpResult.getStatusCode() != Code.OK.getCode()) {
            return otpResult;
        }
        if (user.get().getPassword() == null) {
            return APIResponse.error(Code.BAD_REQUEST.getCode(), "Tài khoản này đã được đăng nhập bằng tài khoản Google. Vui lòng sử dụng tài khoản Google để đăng nhập");
        }
        if (user.get().getEnabled().equals(false)) {
            return APIResponse.error(Code.UNAUTHORIZED.getCode(), "Tài khoản chưa được xác thực. Vui lòng kiểm tra email để xác thực tài khoản");
        }

        String newPassword = "123456789";
        String subject = "Your New Password";
        String content = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".container { background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }" +
                ".header { text-align: center; margin-bottom: 30px; }" +
                ".password-box { background-color: #f5f5f5; padding: 15px; text-align: center; font-size: 32px; letter-spacing: 4px; font-weight: bold; color: #2c3e50; border-radius: 4px; margin: 20px 0; }" +
                ".footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #888; }" +
                ".warning { color: #e74c3c; font-size: 12px; margin-top: 20px; }" +
                ".important { background-color: #fff3cd; color: #856404; padding: 15px; border-radius: 4px; margin: 20px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2 style='color: #2c3e50; margin-bottom: 10px;'>Your Password Has Been Reset</h2>" +
                "<p>Your password has been successfully reset. Here is your new temporary password:</p>" +
                "</div>" +
                "<div class='password-box'>" + newPassword + "</div>" +
                "<div class='important'>" +
                "<p><strong>Important:</strong> For your security, please log in with this temporary password and change it immediately.</p>" +
                "</div>" +
                "<div class='warning'>" +
                "<p>If you didn't request a password reset, please contact our support team immediately as your account may be compromised.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>This is an automated message, please do not reply to this email.</p>" +
                "<p>© " + LocalDateTime.now().getYear() + " Vu Nguyen Coder. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        emailService.sendEmail(email, subject, content);

        user.get().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user.get());

        return APIResponse.success(Code.OK.getCode(), "OTP xác thực thành công. Bạn có thể đặt lại mật khẩu.", null);
    }
    @Override
    public APIResponse updatePassword(String phoneNumber, String newPassword) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isEmpty()) {
            return APIResponse.error(Code.NOT_FOUND.getCode(), "User not found");
        }
        user.get().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user.get());
        return APIResponse.success(Code.OK.getCode(), "Cập nhật mật khẩu thành công", null);
    }

    @Override
    public APIResponse changePasswordByIdentifier(String token, String oldPassword, String newPassword) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return APIResponse.error(Code.NOT_FOUND.getCode(), "Không tìm thấy người dùng với thông tin đã cung cấp");
        }
        if (!passwordEncoder.matches(oldPassword, user.get().getPassword())) {
            return APIResponse.error(Code.BAD_REQUEST.getCode(), "Mật khẩu cũ không chính xác");
        }
        user.get().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user.get());
        return APIResponse.success(Code.OK.getCode(), "Cập nhật mật khẩu thành công", null);
    }
    @Override
    public APIResponse addPhoneNumber(String email, String phoneNumber) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return APIResponse.error(Code.NOT_FOUND.getCode(), "User not found");
        }
        user.get().setPhoneNumber(phoneNumber);
        userRepository.save(user.get());
        return APIResponse.success(Code.OK.getCode(), "Cập nhật số điện thoại thành công", null);
    }

    private final Set<String> invalidTokens = new HashSet<>();

    @Override
    public APIResponse logout(String token) {
        jwtUtils.invalidateToken(token);
        invalidTokens.add(token);
        return APIResponse.success(Code.OK.getCode(), "Logout successfully", null);
    }


    @Override
    public boolean isTokenInvalid(String token) {
        return invalidTokens.contains(token);
    }

    @Override
    public JwtResponse oauth2Callback(String email, String fullName, String avatar) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(fullName);
            newUser.setAvatar(avatar);
            newUser.setMethodLogin("GOOGLE");
            Optional<Role> optionalRole = Optional.ofNullable(roleRepository.findByName("ROLE_USER"));
            if (optionalRole.isEmpty()) {
                return null;
            }
            newUser.setRole(optionalRole.get());
            newUser.setEnabled(Boolean.TRUE);
            newUser.setIsDeleted(Boolean.FALSE);
            userRepository.save(newUser);
            user = userRepository.findByEmail(email);
        }

        ShopUserDetails userDetails = ShopUserDetails.buildUserDetails(user.get());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtTokenForUser(authentication);

//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .toList();

        JwtResponse jwtResponse = JwtResponse.builder()
                .id(user.get().getId().toString())
                .email(user.get().getEmail())
                .fullName(user.get().getFullName())
                .phoneNumber(user.get().getPhoneNumber())
                .address(user.get().getAddress())
                .avatar(user.get().getAvatar())
                .token(jwt)
                .role(RoleResponse.builder()
                        .id(user.get().getRole().getId())
                        .name(user.get().getRole().getName())
                        .build())
                .build();

        return jwtResponse;
    }


}