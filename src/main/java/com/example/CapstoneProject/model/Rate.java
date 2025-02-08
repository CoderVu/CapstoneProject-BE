package com.example.CapstoneProject.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rate")
@Data
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;
    private Double rate;
    private String comment;
    private String createdAt;
    private String updatedAt;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @OneToMany(mappedBy = "rate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ImageRate> imageRatings = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toString();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toString();
    }

}

