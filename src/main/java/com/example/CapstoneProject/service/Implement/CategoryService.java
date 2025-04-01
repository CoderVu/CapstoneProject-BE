package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.request.CategoryRequest;
import com.example.CapstoneProject.mapper.CategoryMapper;
import com.example.CapstoneProject.model.Category;
import com.example.CapstoneProject.repository.CategoryRepository;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.CategoryResponse;
import com.example.CapstoneProject.service.ImageUploadService;
import com.example.CapstoneProject.service.Interface.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageUploadService imageUploadService;
    @Override
    public APIResponse addCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()) != null) {
            return new APIResponse(400, "Danh mục đã tồn tại", false);
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageUrl = imageUploadService.uploadImage(request.getImage());
                category.setImageUrl(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        categoryRepository.save(category);
        return new APIResponse(200, "Thêm danh mục thành công", true);
    }
    @Override
    public APIResponse updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return new APIResponse(404, "Không tìm thấy danh mục", false);
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageUrl = imageUploadService.uploadImage(request.getImage());
                category.setImageUrl(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        categoryRepository.save(category);
        return new APIResponse(200, "Cập nhật danh mục thành công", true);
    }
    @Override
    public APIResponse deleteCategory(String id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return new APIResponse(404, "Không tìm thấy danh mục", false);
        }
        categoryRepository.delete(category);
        return new APIResponse(200, "Xóa danh mục thành công", true);
    }

    @Override
    public List<CategoryResponse> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(CategoryMapper::toCategoryResponse).collect(Collectors.toList());
    }


}