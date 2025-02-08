package com.example.CapstoneProject.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "image_rate")
public class ImageRate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String url;
    @ManyToOne
    @JoinColumn(name = "rate_id")
    private Rate rate;
}
