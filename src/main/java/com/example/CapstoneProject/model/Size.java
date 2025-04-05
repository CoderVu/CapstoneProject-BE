package com.example.CapstoneProject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sizeId;
    private String name;
    private String statusSize;
}