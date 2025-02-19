package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.RateRepository;
import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.response.RoleResponse;
import com.example.CapstoneProject.response.UserResponse;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.service.Interface.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final JwtUtils jwtUtils;
    @Autowired
    private final RateRepository rateRepository;

    @Override
    public JwtResponse getUserInfo(String token) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return null;
        }
        return JwtResponse.builder()
                .id(user.get().getId())
                .fullName(user.get().getFullName())
                .email(user.get().getEmail())
                .phoneNumber(user.get().getPhoneNumber())
                .address(user.get().getAddress())
                .avatar(user.get().getAvatar())
                .methodLogin(user.get().getMethodLogin())
                .role(RoleResponse.builder()
                        .id(user.get().getRole().getId())
                        .name(user.get().getRole().getName())
                        .build())
                .build();
    }
    @Override
    public UserResponse getUserInfoById(String id) {
        Optional<User> user = userRepository.findById(id);
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
                .methodLogin(user.get().getMethodLogin())
                .role(RoleResponse.builder()
                        .id(user.get().getRole().getId())
                        .name(user.get().getRole().getName())
                        .build())
                .build();
    }

}