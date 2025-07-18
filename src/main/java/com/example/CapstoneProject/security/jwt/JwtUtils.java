package com.example.CapstoneProject.security.jwt;

import com.example.CapstoneProject.security.user.ShopUserDetails;
import com.example.CapstoneProject.service.Interface.IAuthService;
import com.example.CapstoneProject.service.Interface.IUserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.security.jwt.signerKey}")
    private String jwtSecret;

    @NonFinal
    @Value("${spring.security.jwt.valid-duration}")
    private int jwtExpirationMs;

    private final IUserService userService;
    private final IAuthService authService;

    public JwtUtils(@Lazy IUserService userService, @Lazy IAuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    public String generateJwtTokenForUser(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        ShopUserDetails foodUserDetails = (ShopUserDetails) userPrincipal;
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return Jwts.builder()
                .claim("phone", foodUserDetails.getPhoneNumber())
                .claim("email", foodUserDetails.getEmail())
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(Instant.now().plus(jwtExpirationMs, ChronoUnit.SECONDS).toEpochMilli()))
                .setIssuer("ShopSystem")
                .setAudience("ShopSystem")
                .setNotBefore(new Date(System.currentTimeMillis()))
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setHeaderParam("kid", "clothes")
                .setId(UUID.randomUUID().toString())
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    public SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUserFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        String phone = claims.get("phone", String.class);
        String email = claims.get("email", String.class);

        if (phone != null) {
            return phone;
        } else if (email != null) {
            return email;
        } else {
            return null;
        }
    }

    public boolean validateToken(String token) {
        if (authService.isTokenInvalid(token)) {
            return false;
        }
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid jwt token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Expired token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("This token is not supported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("No claims found: {}", e.getMessage());
        }
        return false;
    }

    public void invalidateToken(String token) {
        authService.logout(token);

    }
}