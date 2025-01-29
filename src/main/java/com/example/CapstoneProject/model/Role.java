package com.example.CapstoneProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;

@Data
@Entity
@NoArgsConstructor
public class Role {
    public enum name {
        ROLE_USER,
        ROLE_ADMIN,
    }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private Collection<User> users = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    public  String getName(){
        return name != null? name : "";
    }
}