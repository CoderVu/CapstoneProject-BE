package com.example.CapstoneProject.security;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobStorageConfig {

    @Bean
    public BlobServiceClient blobServiceClient() {
        BlobServiceClient client = new BlobServiceClientBuilder()
                .connectionString("DefaultEndpointsProtocol=https;AccountName=dbimage;AccountKey=Mmheb6KKumzJiWclGO937G5pEqGihUBeuMYhwXixVnCLx13dguUeCbsX5J8OhZIHuHrCiT/LD96Y+AStjTASxQ==;EndpointSuffix=core.windows.net")
                .buildClient();
        System.out.println("Connection to Azure Blob Storage successful.");
        return client;
    }
}