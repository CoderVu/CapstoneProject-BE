package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.request.RegisterRequest;
import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.Role;
import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.RoleRepository;
import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.security.user.ShopUserDetails;
import com.example.CapstoneProject.service.Interface.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public JwtResponse authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByPhoneNumber(username);
        if (user.isEmpty()) {
            user = userRepository.findByEmail(username);
        }
        if (user.isEmpty()) {
            return null;
        }
        if (user.get().getPassword() == null) {
            return null;
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtTokenForUser(authentication);
            ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            JwtResponse jwtResponse = JwtResponse.builder()
                    .id(user.get().getId().toString())
                    .email(user.get().getEmail())
                    .fullName(user.get().getFullName())
                    .phoneNumber(user.get().getPhoneNumber())
                    .address(user.get().getAddress())
                    .avatar(user.get().getAvatar())
                    .token(jwt)
                    .roles(roles)
                    .build();
            return jwtResponse;
        } catch (BadCredentialsException e) {
            return null;
        }
    }
    @Override
    public APIResponse registerUser(RegisterRequest request) {
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
        userRepository.save(user);
        return APIResponse.success(Code.CREATED.getCode(), "Register successfully", null);
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
            userRepository.save(newUser);
            user = userRepository.findByEmail(email);
        }

        ShopUserDetails userDetails = ShopUserDetails.buildUserDetails(user.get());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtTokenForUser(authentication);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtResponse jwtResponse = JwtResponse.builder()
                .id(user.get().getId().toString())
                .email(user.get().getEmail())
                .fullName(user.get().getFullName())
                .phoneNumber(user.get().getPhoneNumber())
                .address(user.get().getAddress())
                .avatar(user.get().getAvatar())
                .token(jwt)
                .roles(roles)
                .build();

        return jwtResponse;
    }


}