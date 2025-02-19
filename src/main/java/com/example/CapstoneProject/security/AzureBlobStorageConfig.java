package com.example.CapstoneProject.security;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobStorageConfig {

    @Bean
    public BlobServiceClient blobServiceClient() {
        // String AccountKey = System.getenv("azure.storage.account.key").trim();
        String AccountKey = "Mmheb6KKumzJiWclGO937G5pEqGihUBeuMYhwXixVnCLx13dguUeCbsX5J8OhZIHuHrCiT/LD96Y+AStjTASxQ==";
        if (AccountKey.isEmpty()) {
            throw new IllegalArgumentException("Azure Storage Account Key is missing.");
        }
        return new BlobServiceClientBuilder()
                .connectionString("DefaultEndpointsProtocol=https;AccountName=dbimage;AccountKey=" + AccountKey + ";EndpointSuffix=core.windows.net")
                .buildClient();
    }
}