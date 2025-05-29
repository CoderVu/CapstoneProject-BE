package com.example.CapstoneProject.security;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobStorageConfig {

    @Bean
    public BlobServiceClient blobServiceClient() {
        String AccountKey = System.getenv("AZURE_STORAGE_ACCOUNT_KEY").trim();
//        String AccountKey = "";
        if (AccountKey.isEmpty()) {
            throw new IllegalArgumentException("Azure Storage Account Key is missing.");
        }
        return new BlobServiceClientBuilder()
                .connectionString("DefaultEndpointsProtocol=https;AccountName=dbimage;AccountKey=" + AccountKey + ";EndpointSuffix=core.windows.net")
                .buildClient();
    }
}