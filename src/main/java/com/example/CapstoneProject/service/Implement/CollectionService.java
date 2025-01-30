package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.Request.CollectionRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.mapper.CollectionMapper;
import com.example.CapstoneProject.model.Collection;
import com.example.CapstoneProject.model.Product;
import com.example.CapstoneProject.repository.CollectionRepository;
import com.example.CapstoneProject.repository.ProductRepository;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.ICollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectionService implements ICollectionService {

    @Autowired
    private CollectionRepository collectionRepository;
    @Autowired
    private CollectionMapper collectionMapper;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public APIResponse addCollection(CollectionRequest request) {
        return collectionMapper.toCollection(request);
    }

    @Override
    public APIResponse addProductToCollection(String collectionId, String productId) {
        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (collection == null) {
            return new APIResponse(Code.NOT_FOUND.getCode(), "Collection not found", false);
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return new APIResponse(Code.NOT_FOUND.getCode(), "Product not found", false);
        }
        collection.getProducts().add(product);
        collectionRepository.save(collection);
        return new APIResponse(Code.OK.getCode(), "Product added to collection", true);
    }
}
