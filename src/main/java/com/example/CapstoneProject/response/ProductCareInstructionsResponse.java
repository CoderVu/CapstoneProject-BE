package com.example.CapstoneProject.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCareInstructionsResponse {
    private String productId;
    private Map<String, String> attributes;
    private String description;
}
