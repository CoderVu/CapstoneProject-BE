package com.example.CapstoneProject.security.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.CapstoneProject.model.User;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopUserDetails implements UserDetails {
    private String id;
    private String phoneNumber;
    private String password;
    private String fullName;
    private String email;
    private String sub;
    private String facebookId;
    private String address;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Collection<GrantedAuthority> authorities;

    public static ShopUserDetails buildUserDetails(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());
        return new ShopUserDetails(
                user.getId(),
                user.getPhoneNumber(),
                user.getPassword(),
                user.getFullName(),
                user.getEmail(),
                user.getSub(),
                user.getFacebookId(),
                user.getAddress(),
                user.getAvatar(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                List.of(authority));
    }

    public static String getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((ShopUserDetails) userDetails).getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    } // Trả về trạng thái hết hạn của thông tin đăng nhập

    @Override
    public boolean isEnabled() {
        return true;
    }
}
