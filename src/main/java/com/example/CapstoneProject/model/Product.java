package com.example.CapstoneProject.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String productName;
    private String description;
    private Integer price;
    @ManyToOne()
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();
    private Boolean newProduct = false;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "main_image_id", referencedColumnName = "id")
    private Image mainImage;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> additionalImages = new ArrayList<>();
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productname='" + productName + '\'' +
                '}';
    }
}