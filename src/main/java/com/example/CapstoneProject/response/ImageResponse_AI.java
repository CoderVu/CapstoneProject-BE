package com.example.CapstoneProject.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageResponse_AI {
    private String id;
    private String path;
    private String productId;
    private String productName;
    private List<Double> vectorFeatures;

}

