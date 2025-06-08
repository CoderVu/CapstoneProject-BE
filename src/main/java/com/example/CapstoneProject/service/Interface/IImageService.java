package com.example.CapstoneProject.service.Interface;

import org.springframework.transaction.annotation.Transactional;

public interface IImageService {
    @Transactional
    boolean addVectorToImage(String imageId, String vectorFeatures);

    @Transactional
    boolean updateVectorToImage(String imageId, String vectorFeatures);
}
