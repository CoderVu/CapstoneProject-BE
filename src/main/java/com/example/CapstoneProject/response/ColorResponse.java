package com.example.CapstoneProject.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorResponse {
    private String id;
    private String color;
    private String colorCode;
}
