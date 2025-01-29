package com.example.CapstoneProject.security;

import com.example.CapstoneProject.response.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.util.Map;
@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<APIResponse> handleMissingParams(MissingServletRequestParameterException ex, WebRequest request) {
        String paramName = ex.getParameterName();
        APIResponse apiResponse = APIResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Required request parameter '" + paramName + "' is missing")
                .data(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Bad Request",
                        "path", request.getDescription(false)
                ))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public ResponseEntity<APIResponse> handleAsyncRequestNotUsableException(AsyncRequestNotUsableException ex) {
        APIResponse apiResponse = APIResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Request not usable: " + ex.getMessage())
                .data(Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "path", "N/A"
                ))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<APIResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        APIResponse apiResponse = APIResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An error occurred: " + ex.getMessage())
                .data(Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "path", request.getDescription(false)
                ))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleGenericException(Exception ex, WebRequest request) {
        APIResponse apiResponse = APIResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred: " + ex.getMessage())
                .data(Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "path", request.getDescription(false)
                ))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<APIResponse> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex) {
        APIResponse apiResponse = APIResponse.builder()
                .statusCode(HttpStatus.REQUEST_TIMEOUT.value())
                .message("Request timeout: " + ex.getMessage())
                .data(Map.of(
                        "status", HttpStatus.REQUEST_TIMEOUT.value(),
                        "error", "Request Timeout",
                        "path", "N/A"
                ))
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.REQUEST_TIMEOUT);
    }
}