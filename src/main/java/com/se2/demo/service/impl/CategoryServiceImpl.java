package com.se2.demo.service.impl;

import com.se2.demo.dto.request.CategoryRequest;
import com.se2.demo.dto.response.CategoryResponse;
import com.se2.demo.mapper.CommonMapper;
import com.se2.demo.model.entity.Category;
import com.se2.demo.repository.CategoryRepository;
import com.se2.demo.service.CategoryService;
import com.se2.demo.service.CloudinaryService;
import com.se2.demo.utils.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CommonMapper commonMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return commonMapper.toCategoryResponseList(categoryRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return commonMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = commonMapper.toEntity(request);

        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(request.getImageFile(), "categories");
            category.setImageUrl(imageUrl);
        }

        // Handle parent category logic
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent Category not found with id: " + request.getParentId()));
            category.setParentCategory(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return commonMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Integer id, CategoryRequest request) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        existingCategory.setName(request.getName());
        existingCategory.setSlug(request.getSlug());

        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(request.getImageFile(), "categories");
            existingCategory.setImageUrl(imageUrl);
        }

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent Category not found with id: " + request.getParentId()));
            existingCategory.setParentCategory(parent);
        } else {
            existingCategory.setParentCategory(null);
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        return commonMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(existingCategory);
    }
}
