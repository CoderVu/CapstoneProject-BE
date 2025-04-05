package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SizeRepository extends JpaRepository<Size, String> {
    Size findByName(String name);
    boolean existsByName(String name);
}
