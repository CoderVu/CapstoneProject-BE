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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
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
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
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
            return APIResponse.error(Code.BAD_REQUEST.getCode(), "Invalid phone number format");
        }

        // Validate email
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; // Standard email format
        if (!request.getEmail().matches(emailRegex)) {
            return APIResponse.error(Code.BAD_REQUEST.getCode(), "Invalid email format");
        }

        // Check if phone number or email already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return APIResponse.error(Code.CONFLICT.getCode(), request.getPhoneNumber() + " already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return APIResponse.error(Code.CONFLICT.getCode(), request.getEmail() + " already exists");
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
        String subject = "Sign in to your account";
        String content = "<h2>Sign in to your account</h2>" +
                "<p>You requested to sign in to Vu Nguyen Coder.<br>Your one-time code is:</p>" +
                "<h1 style='font-size:32px; letter-spacing:4px;'>" + otp.getOtp() + "</h1>" +
                "<p>This code expires in <strong>5 minutes</strong>.</p>" +
                "<br><p style='font-size:12px; color:#888;'>Email sent by Vu Nguyen Coder</p>" +
                "<p style='font-size:12px; color:#888;'>If you didn’t request to sign in to Shop, please ignore this email.</p>";

        emailService.sendEmail(request.getEmail(), subject, content);
        return APIResponse.success(Code.CREATED.getCode(), "Vui lòng kiểm tra email để xác thực tài khoản", null);
    }
    @Override
    public APIResponse updatePassword(String phoneNumber, String newPassword) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isEmpty()) {
            return APIResponse.error(Code.NOT_FOUND.getCode(), "User not found");
        }
        user.get().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user.get());
        return APIResponse.success(Code.OK.getCode(), "Update password successfully", null);
    }
    @Override
    public APIResponse addPhoneNumber(String email, String phoneNumber) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return APIResponse.error(Code.NOT_FOUND.getCode(), "User not found");
        }
        user.get().setPhoneNumber(phoneNumber);
        userRepository.save(user.get());
        return APIResponse.success(Code.OK.getCode(), "Add phone number successfully", null);
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