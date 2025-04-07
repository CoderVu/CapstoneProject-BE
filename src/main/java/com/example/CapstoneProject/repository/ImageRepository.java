package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, String> {

    @Query("SELECT i FROM Image i WHERE i.url IN :imgUrls")
    Page<Image> findByImageUrlIn(@Param("imgUrls") List<String> imgUrls, Pageable pageable);
}
