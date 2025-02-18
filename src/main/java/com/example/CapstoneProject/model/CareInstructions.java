package com.example.CapstoneProject.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "care_instructions")
public class CareInstructions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(columnDefinition = "TEXT")
    private String careDetails; // Lưu trữ hướng dẫn chăm sóc dưới dạng JSON
}
