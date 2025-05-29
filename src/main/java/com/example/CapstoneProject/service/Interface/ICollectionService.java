package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.CollectionRequest;
import com.example.CapstoneProject.response.APIResponse;

public interface ICollectionService {
    APIResponse addCollection(CollectionRequest request);

    APIResponse addProductToCollection(String collectionId, String productId);

    APIResponse getAllCollections();
}
