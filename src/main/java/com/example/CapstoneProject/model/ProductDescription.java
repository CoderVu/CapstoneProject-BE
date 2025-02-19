package com.example.CapstoneProject.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product_description")
public class ProductDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(columnDefinition = "TEXT")
    private String attributes; // Lưu trữ dữ liệu dưới dạng JSON

    @Column(columnDefinition = "TEXT")
    private String description; // Lưu trữ dữ liệu dưới dạng JSON
}
