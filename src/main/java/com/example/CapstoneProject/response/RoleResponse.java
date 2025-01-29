package com.example.CapstoneProject.response;

import lombok.Data;

@Data
public class RoleResponse {
    private String id;
    private String name;

    public RoleResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
