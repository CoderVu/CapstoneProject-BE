package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.Request.BrandRequest;
import com.example.CapstoneProject.Request.CollectionRequest;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.Product;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.ICollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/collection")
@RequiredArgsConstructor
public class CollectionController {

    @Autowired
    private ICollectionService collectionService;


    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> addCollection(@RequestBody CollectionRequest request) {
        APIResponse response = collectionService.addCollection(request);
        if (response.getStatusCode() == Code.OK.getCode()) {
            return ResponseEntity.ok(new APIResponse(Code.OK.getCode(), "Collection added", true));
        }
        return ResponseEntity.ok(new APIResponse(Code.BAD_REQUEST.getCode(), "Collection already exists", false));
    }

    @PostMapping(value = "/addProduct", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> addProductToCollection(@RequestBody CollectionRequest request) {
        APIResponse response = collectionService.addProductToCollection(request.getCollectionId(), request.getProductId());
        if (response.getStatusCode() == Code.OK.getCode()) {
            return ResponseEntity.ok(new APIResponse(Code.OK.getCode(), "Product added to collection", true));
        }
        return ResponseEntity.ok(new APIResponse(Code.BAD_REQUEST.getCode(), "Product already exists in collection", false));
    }
}