package com.example.CapstoneProject.service;

import org.springframework.data.jpa.domain.Specification;
import com.example.CapstoneProject.model.Product;

public class ProductSpecification {

    public static Specification<Product> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category").get("name"), category);
    }

    public static Specification<Product> hasBrand(String brand) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("brand").get("name"), brand);
    }
    public static Specification<Product> hasPrice(Double min, Double max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("price"), min, max);
    }

    public static Specification<Product> hasColor(String color) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("variants").join("color").get("color"), color);
    }

    public static Specification<Product> hasSize(String size) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("variants").join("size").get("name"), size);
    }
}