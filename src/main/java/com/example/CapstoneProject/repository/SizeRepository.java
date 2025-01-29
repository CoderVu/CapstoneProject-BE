package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<Size, String> {
    Size findByName(String name);
}
