package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
    Category findByName(String name);
}
