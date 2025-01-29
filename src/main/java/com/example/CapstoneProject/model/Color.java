package com.example.CapstoneProject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String colorId;
    private String color;
    private String colorCode;
}