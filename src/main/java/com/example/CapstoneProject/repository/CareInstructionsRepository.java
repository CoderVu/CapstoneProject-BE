package com.example.CapstoneProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CapstoneProject.model.CareInstructions;

public interface CareInstructionsRepository extends JpaRepository<CareInstructions, String> {

    boolean existsByProductId(String productId);

    CareInstructions findByProductId(String productId);
  
}