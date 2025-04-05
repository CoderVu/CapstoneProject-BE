package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FavoriteProductRepository extends JpaRepository<Favorite, Long> {

}
