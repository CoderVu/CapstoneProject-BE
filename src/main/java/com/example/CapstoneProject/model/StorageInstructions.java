package com.example.CapstoneProject.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

@Data
@Entity
@Table(name = "storage_instructions")
public class StorageInstructions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(columnDefinition = "TEXT")
    private String storageDetails; // Lưu trữ hướng dẫn bảo quản dưới dạng JSON
}
