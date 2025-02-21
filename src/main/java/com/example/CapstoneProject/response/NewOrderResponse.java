package com.example.CapstoneProject.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NewOrderResponse {
    private String id;
    private String orderCode;
    private String userName;
    private LocalDateTime orderDate;

}