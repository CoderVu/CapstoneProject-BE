package com.example.CapstoneProject.security;

import com.example.CapstoneProject.security.jwt.AuthTokenFilter;
import com.example.CapstoneProject.security.jwt.JwtAuthEntryPoint;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.security.user.ShopUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig {

        private final ShopUserDetailsService userDetailsService;
        private final JwtAuthEntryPoint jwtAuthEntryPoint;
        private final JwtUtils jwtUtils;

        private static final String[] AUTH = {
                        "/api/v1/auth/**"
        };
        private static final String[] ADMIN = {
                        "/api/v1/admin/**"
        };
        private static final String[] USER = {
                        "/api/v1/user/**"
        };
        private static final String[] PUBLIC = {
                        "/api/v1/public/**"
        };
        private static final String[] MOMO = {
                        "/api/v1/momo/**"
        };
        private static final String[] ZALO = {
                        "/api/v1/zalopay/**"
        };
        private static final String[] EMAIL = {
                        "/api/v1/email/**"
        };
        private static final String[] PAYMENT = {
                        "/api/analyze/**"
        };
        private static final String[] WS = {
                        "/ws/**"
        };

        @Autowired
        public WebSecurityConfig(@Lazy JwtUtils jwtUtils, JwtAuthEntryPoint jwtAuthEntryPoint,
                        ShopUserDetailsService userDetailsService) {
                this.jwtUtils = jwtUtils;
                this.jwtAuthEntryPoint = jwtAuthEntryPoint;
                this.userDetailsService = userDetailsService;
        }

        @Bean
        public AccessDeniedHandler accessDeniedHandler() {
                return (request, response, accessDeniedException) -> response.sendError(HttpStatus.FORBIDDEN.value(),
                                "Access Denied");
        }

        @Bean
        public AuthTokenFilter authTokenFilter() {
                return new AuthTokenFilter(jwtUtils, userDetailsService);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                var authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(AUTH).permitAll()
                                                .requestMatchers(PUBLIC).permitAll()
                                                .requestMatchers(EMAIL).permitAll()
                                                .requestMatchers(MOMO).permitAll()
                                                .requestMatchers(ZALO).permitAll()
                                                .requestMatchers(USER).permitAll()
                                                .requestMatchers(PAYMENT).permitAll()
                                                .requestMatchers(WS).permitAll()
                                                .requestMatchers(ADMIN).hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler()))
                        .oauth2Login(oauth2 -> oauth2
                                .failureUrl("/api/v1/auth/login?error")
                        )
                                .sessionManagement(session -> session
                                                .maximumSessions(Integer.MAX_VALUE)
                                                .maxSessionsPreventsLogin(true));

                http.authenticationProvider(authenticationProvider());
                http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }
}

