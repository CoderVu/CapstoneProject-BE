package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.request.CategoryRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.service.Implement.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping("/add")
    public ResponseEntity<APIResponse> addBrand(@RequestBody CategoryRequest request) {
        boolean isAdded = categoryService.addCategory(request);
        if (isAdded) {
            return ResponseEntity.status(Code.CREATED.getCode())
                    .body(new APIResponse(Code.CREATED.getCode(), Code.CREATED.getMessage(), ""));
        } else {
            return ResponseEntity.status(Code.CONFLICT.getCode())
                    .body(new APIResponse(Code.CONFLICT.getCode(), Code.CONFLICT.getMessage(), ""));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse> updateBrand(@PathVariable String id, @RequestBody CategoryRequest request) {
        categoryService.updateCategory(id, request);
        return ResponseEntity.status(Code.OK.getCode())
                .body(new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), ""));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> deleteBrand(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(Code.OK.getCode())
                .body(new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), ""));
    }
}