package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, String> {
    Brand findByName(String name);
}
