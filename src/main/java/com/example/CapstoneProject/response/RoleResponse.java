package com.example.CapstoneProject.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponse {
    private String id;
    private String name;
}
