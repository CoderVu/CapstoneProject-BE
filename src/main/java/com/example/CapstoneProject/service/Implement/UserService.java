package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.response.RoleResponse;
import com.example.CapstoneProject.response.UserResponse;
import com.example.CapstoneProject.security.user.ShopUserDetailsService;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.service.Interface.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    public UserResponse getUserInfo(String token) {
        String username = jwtUtils.getUserNameFromToken(token);
        Optional<User> user = userRepository.findByPhoneNumber(username);
        if (user.isEmpty()) {
            user = userRepository.findByEmail(username);
        }
        if (user.isEmpty()) {
            return null;
        }
        return UserResponse.builder()
                .id(user.get().getId())
                .fullName(user.get().getFullName())
                .email(user.get().getEmail())
                .phoneNumber(user.get().getPhoneNumber())
                .address(user.get().getAddress())
                .avatar(user.get().getAvatar())
                .role(RoleResponse.builder()
                        .id(user.get().getRole().getId())
                        .name(user.get().getRole().getName())
                        .build())
                .build();
    }
}