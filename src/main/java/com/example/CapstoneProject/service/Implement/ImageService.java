package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.Image;
import com.example.CapstoneProject.repository.ImageRepository;
import com.example.CapstoneProject.service.Interface.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageService implements IImageService {

    @Autowired
    private ImageRepository imageRepository;
    @Transactional
    @Override
    public boolean addVectorToImage(String imageId, String vectorFeatures) {
        Image image = imageRepository.findById(imageId).orElse(null);
        if (image == null) {
            return false;
        }
        image.setVectorFeatures(vectorFeatures);
        imageRepository.save(image);
        return true;
    }
    @Transactional
    @Override
    public boolean updateVectorToImage(String imageId, String vectorFeatures) {
        Image image = imageRepository.findById(imageId).orElse(null);
        if (image == null) {
            return false;
        }
        image.setVectorFeatures(vectorFeatures);
        imageRepository.save(image);
        return true;
    }
}
