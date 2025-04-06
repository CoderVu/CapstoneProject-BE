package com.example.CapstoneProject.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobStorageException;
import com.example.CapstoneProject.utils.ImageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageUploadService {
    private final BlobServiceClient blobServiceClient;

    @Autowired
    public ImageUploadService(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String fileName = generateFileName(file);
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("images");

        BlobClient blobClient = containerClient.getBlobClient(fileName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);
        blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(file.getContentType()));
        return blobClient.getBlobUrl();

    }

    public List<ImageData> downloadAllImages() {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("images");
        List<ImageData> imageDataList = new ArrayList<>();

        for (BlobItem blobItem : containerClient.listBlobs()) {
            BlobClient blobClient = containerClient.getBlobClient(blobItem.getName());
            BlobProperties properties = blobClient.getProperties();
            InputStream inputStream = blobClient.openInputStream();

            ImageData imageData = new ImageData(
                    blobItem.getName(),
                    properties.getLastModified().toString(),
                    properties.getCreationTime().toString(),
                    properties.getContentType(),
                    inputStream
            );

            imageDataList.add(imageData);
        }

        return imageDataList;
    }

    public void deleteImage(String fileUrl) {
        String blobName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        System.out.println("Deleting image " + blobName);
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("images");
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        try {
            blobClient.delete();
        } catch (BlobStorageException e) {
            if (e.getStatusCode() == 404) {
                System.out.println("Blob not found: " + blobName);
            } else {
                throw e;
            }
        }
    }
    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID() + "-" + file.getOriginalFilename();
    }
}