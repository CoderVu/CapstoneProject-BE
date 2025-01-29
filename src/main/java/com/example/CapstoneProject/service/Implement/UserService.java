package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.security.user.ShopUserDetailsService;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.service.Interface.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShopUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
}