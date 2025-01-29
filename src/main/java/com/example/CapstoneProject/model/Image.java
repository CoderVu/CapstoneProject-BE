package com.example.CapstoneProject.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String url;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}