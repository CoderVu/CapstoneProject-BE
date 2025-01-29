package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<Color, String> {
    boolean existsByColor(String name);

    Color findByColor(String name);
}
