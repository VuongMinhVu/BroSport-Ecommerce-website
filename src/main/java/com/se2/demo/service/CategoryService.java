package com.se2.demo.service;

import com.se2.demo.dto.request.CategoryRequest;
import com.se2.demo.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Integer id);

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Integer id, CategoryRequest request);

    void deleteCategory(Integer id);
}
