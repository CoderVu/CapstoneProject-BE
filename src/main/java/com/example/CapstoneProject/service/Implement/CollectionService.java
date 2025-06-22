package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.request.CollectionRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.mapper.CollectionMapper;
import com.example.CapstoneProject.model.Collection;
import com.example.CapstoneProject.model.Product;
import com.example.CapstoneProject.repository.CollectionRepository;
import com.example.CapstoneProject.repository.ProductRepository;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.CollectionResponse;
import com.example.CapstoneProject.service.Interface.ICollectionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Transactional
    @Override
    public APIResponse addProductToCollection(String collectionId, String productId) {
        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (collection == null) {
            return new APIResponse(Code.NOT_FOUND.getCode(), "Không tìm thấy bộ sưu tập", "");
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return new APIResponse(Code.NOT_FOUND.getCode(), "Sản phẩm không tồn tại", "");
        }

        if (collection.getProducts().contains(product)) {
            return new APIResponse(Code.BAD_REQUEST.getCode(), "Sản phẩm đã tồn tại trong bộ sưu tập", "");
        }

        collection.getProducts().add(product);
        product.getCollections().add(collection); // maintain both sides

        collectionRepository.save(collection);
        productRepository.save(product); // save both entities

        return new APIResponse(Code.OK.getCode(), "Thêm sản phẩm vào bộ sưu tập thành công", "");
    }
    @Override
    public APIResponse getAllCollections() {
        List<CollectionResponse> collections = collectionRepository.findAll()
                .stream()
                .map(collectionMapper::toCollectionResponse)
                .toList();
        return new APIResponse(Code.OK.getCode(), "Lấy tất cả bộ sưu tập thành công", collections);
    }
    @Override
    public APIResponse updateCollection(String collectionId, CollectionRequest request) {
        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (collection == null) {
            return new APIResponse(Code.NOT_FOUND.getCode(), "Bộ sưu tập không tồn tại", "");
        }
        collection.setName(request.getCollectionName());
        // Update other fields as needed
        collectionRepository.save(collection);
        return new APIResponse(Code.OK.getCode(), "Cập nhật bộ sưu tập thành công", "");
    }
    @Transactional
    @Override
    public APIResponse removeProductFromCollection(String collectionId, String productId) {
        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (collection == null) {
            return new APIResponse(Code.NOT_FOUND.getCode(), "Collection not found", false);
        }
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return new APIResponse(Code.NOT_FOUND.getCode(), "Product not found", false);
        }
        boolean removed = collection.getProducts().remove(product);
        if (!removed) {
            return new APIResponse(Code.BAD_REQUEST.getCode(), "Product not in collection", false);
        }

        product.getCollections().remove(collection); // maintain both sides
        collectionRepository.save(collection);
        productRepository.save(product); // save both entities
        return new APIResponse(Code.OK.getCode(), "Sản phẩm đã được xóa khỏi bộ sưu tập", "");

    }
    @Override
    public APIResponse deleteCollection(String collectionId) {
        Collection collection = collectionRepository.findById(collectionId).orElse(null);
        if (collection == null) {
            return new APIResponse(Code.NOT_FOUND.getCode(), "Bộ sưu tập không tồn tại", "");
        }
        collectionRepository.delete(collection);
        return new APIResponse(Code.OK.getCode(), "Xóa bộ sưu tập thành công", "");
    }

}

