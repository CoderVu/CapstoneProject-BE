package com.example.CapstoneProject.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionRequest {
    private String CollectionId;
    private String productId;
    private String collectionName;

}
