package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.request.CollectionRequest;
import com.example.CapstoneProject.StatusCode.Code;
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
            return ResponseEntity.ok(new APIResponse(Code.OK.getCode(), "Collection added", ""));
        }
        return ResponseEntity.ok(new APIResponse(Code.BAD_REQUEST.getCode(), "Collection already exists", ""));
    }

    @PostMapping(value = "/{collectionId}/product/{productId}", produces = "application/json")
    public ResponseEntity<APIResponse> addProductToCollection(
            @PathVariable String collectionId,
            @PathVariable String productId) {
        APIResponse response = collectionService.addProductToCollection(collectionId, productId);
       return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponse> updateCollection(@RequestBody CollectionRequest request) {
        APIResponse response = collectionService.updateCollection(request.getCollectionId(), request);
        if (response.getStatusCode() == Code.OK.getCode()) {
            return ResponseEntity.ok(new APIResponse(Code.OK.getCode(), "Cập nhật bộ sưu tập thành công", ""));
        }
        return ResponseEntity.status(Code.NOT_FOUND.getCode())
                .body(new APIResponse(Code.NOT_FOUND.getCode(), "Bộ sưu tập không tồn tại", ""));
    }

    @DeleteMapping(value = "/{collectionId}/product/{productId}", produces = "application/json")
    public ResponseEntity<APIResponse> removeProductFromCollection(
            @PathVariable String collectionId,
            @PathVariable String productId) {
        APIResponse response = collectionService.removeProductFromCollection(collectionId, productId);
        if (response.getStatusCode() == Code.OK.getCode()) {
            return ResponseEntity.ok(new APIResponse(Code.OK.getCode(), "Product removed from collection", ""));
        }
        return ResponseEntity.status(Code.BAD_REQUEST.getCode())
                .body(new APIResponse(Code.BAD_REQUEST.getCode(), response.getMessage(), ""));
    }
    @GetMapping("/all")
    public ResponseEntity<APIResponse> getAllCollections() {
        APIResponse response = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), collectionService.getAllCollections());
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{collectionId}")
    public ResponseEntity<APIResponse> deleteCollection(@PathVariable String collectionId) {
        APIResponse response = collectionService.deleteCollection(collectionId);
        if (response.getStatusCode() == Code.OK.getCode()) {
            return ResponseEntity.ok(new APIResponse(Code.OK.getCode(), "Collection deleted successfully", ""));
        }
        return ResponseEntity.status(Code.NOT_FOUND.getCode())
                .body(new APIResponse(Code.NOT_FOUND.getCode(), "Collection not found", ""));
    }


}