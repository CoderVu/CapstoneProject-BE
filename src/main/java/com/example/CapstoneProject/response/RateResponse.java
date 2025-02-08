package com.example.CapstoneProject.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateResponse {
    private String id;
    private String userId;
    private String productId;
    private Double rate;
    private String comment;
    private String createdAt;
    private String updatedAt;
    private List<String> imageRatings;
}
