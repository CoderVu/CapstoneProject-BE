package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository <Collection, String> {

    Collection findByName(String name);
}
