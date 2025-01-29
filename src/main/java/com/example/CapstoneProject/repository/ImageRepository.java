package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, String> {
}
