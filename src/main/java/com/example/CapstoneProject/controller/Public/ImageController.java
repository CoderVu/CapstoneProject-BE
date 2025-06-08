package com.example.CapstoneProject.controller.Public;

import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.VectorFeatureRequest;
import com.example.CapstoneProject.service.ImageUploadService;
import com.example.CapstoneProject.service.Interface.IImageService;
import com.example.CapstoneProject.utils.ImageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/public")
public class ImageController {
    @Autowired
    private ImageUploadService imageUploadService;
    @Autowired
    private IImageService imageService;


    @Autowired
    public ImageController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @PostMapping("/image/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imageUploadService.uploadImage(file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
        }
    }


    @GetMapping("/image/download")
    public ResponseEntity<APIResponse> downloadAllImages() {
        try {
            List<ImageData> images = imageUploadService.downloadAllImages();

            // Extract only the image name for the response
            List<String> imageNames = images.stream()
                    .map(image -> image.getName()) // Assuming getName() returns the image name
                    .collect(Collectors.toList());

            // Check if no images are found
            if (imageNames.isEmpty()) {
                return ResponseEntity.status(404).body(new APIResponse(404, "No images found", null));
            }

            APIResponse response = new APIResponse(200, "Images downloaded successfully", imageNames);
            return ResponseEntity.ok(response);
        } catch (Exception e) {

            return ResponseEntity.status(500).body(new APIResponse(500, "An unexpected error occurred: " + e.getMessage(), null));
        }
    }
    @PostMapping("/image/add/vector_feature")
    public ResponseEntity<?> addVectorFeature(@RequestBody VectorFeatureRequest request) {
        boolean result = imageService.addVectorToImage(request.getId(), request.getVectorFeatures());

        if (result) {
            return ResponseEntity.ok("✅ Vector feature added successfully.");
        } else {
            return ResponseEntity.badRequest().body("❌ Image not found with ID: " + request.getId());
        }
    }
    @PutMapping("/image/update/vector_feature")
    public ResponseEntity<?> updateVectorFeature(@RequestBody VectorFeatureRequest request) {
        boolean result = imageService.updateVectorToImage(request.getId(), request.getVectorFeatures());

        if (result) {
            return ResponseEntity.ok("✅ Vector feature updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("❌ Image not found with ID: " + request.getId());
        }
    }


}