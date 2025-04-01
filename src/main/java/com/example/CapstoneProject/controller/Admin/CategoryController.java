package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.request.CategoryRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.service.Implement.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping("/add")
    public ResponseEntity<APIResponse> addCategory(@RequestParam("name") String name,
                                                    @RequestParam("description") String description,
                                                   @RequestParam("image") MultipartFile image) {
        CategoryRequest request = new CategoryRequest();
        request.setName(name);
        request.setDescription(description);
        request.setImage(image);
        APIResponse response = categoryService.addCategory(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse> updateBrand(@PathVariable String id,
                                                    @RequestParam(value = "name", required = false) String name,
                                                    @RequestParam(value = "description" , required = false) String description,
                                                    @RequestParam(value = "image", required = false) MultipartFile image ) {
          CategoryRequest request = new CategoryRequest();
          request.setName(name);
            request.setDescription(description);
          request.setImage(image);
          APIResponse response = categoryService.updateCategory(id, request);
          return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> deleteBrand(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(Code.OK.getCode())
                .body(new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), ""));
    }
}