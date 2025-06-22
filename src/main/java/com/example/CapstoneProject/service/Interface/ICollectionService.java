package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.CollectionRequest;
import com.example.CapstoneProject.response.APIResponse;

public interface ICollectionService {
    APIResponse addCollection(CollectionRequest request);

    APIResponse addProductToCollection(String collectionId, String productId);

    APIResponse getAllCollections();

    APIResponse updateCollection(String collectionId, CollectionRequest request);

    APIResponse removeProductFromCollection(String collectionId, String productId);

    APIResponse deleteCollection(String collectionId);
}
