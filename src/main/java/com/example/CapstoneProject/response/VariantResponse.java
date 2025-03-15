package com.example.CapstoneProject.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class

VariantResponse {
    private String id;
    private String sizeName;
    private String color;
    private Integer quantity;
}