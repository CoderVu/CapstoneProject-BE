package com.example.CapstoneProject.mapper;

import com.example.CapstoneProject.request.CollectionRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.Collection;
import com.example.CapstoneProject.repository.CollectionRepository;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.CollectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

    @Autowired
    private CollectionRepository collectionRepository;


    public APIResponse toCollection(CollectionRequest request) {
        Collection existingCollection = collectionRepository.findByName(request.getCollectionName());
        if (existingCollection != null) {
            return new APIResponse(Code.BAD_REQUEST.getCode(),"Collection already exists", false);
        }
        Collection collection = new Collection();
        collection.setName(request.getCollectionName());
        collectionRepository.save(collection);
        return new APIResponse(Code.OK.getCode(), "Collection added", true);
    }
        public CollectionResponse toCollectionResponse(Collection collection) {
        return CollectionResponse.builder()
                .id(collection.getId())
                .name(collection.getName())
                .build();
    }
}
