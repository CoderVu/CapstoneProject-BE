package com.example.CapstoneProject.security.jwt;

import com.example.CapstoneProject.response.APIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Check if the error is 404 (Not Found)
        if (response.getStatus() == HttpServletResponse.SC_NOT_FOUND) {
            handleNotFound(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ResponseEntity<APIResponse> responseEntity = buildResponseEntity(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized",
                    authException.getMessage(),
                    request.getServletPath()
            );
            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), responseEntity.getBody());
        }
    }

    private void handleNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        ResponseEntity<APIResponse> responseEntity = buildResponseEntity(
                HttpServletResponse.SC_NOT_FOUND,
                "Not Found",
                "The requested URL was not found on this server.",
                request.getServletPath()
        );

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), responseEntity.getBody());
    }

    private ResponseEntity<APIResponse> buildResponseEntity(int status, String error, String message, String path) {
        APIResponse apiResponse = APIResponse.builder()
                .statusCode(status)
                .message(message)
                .data(Map.of(
                        "status", status,
                        "error", error,
                        "path", path
                ))
                .build();
        return ResponseEntity.status(status).body(apiResponse);
    }
}