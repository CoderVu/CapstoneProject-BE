package com.example.CapstoneProject.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String city;
    private String district;
    private String houseNumber;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}