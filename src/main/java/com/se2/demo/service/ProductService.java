package com.se2.demo.service;

import com.se2.demo.dto.request.ProductRequest;
import com.se2.demo.dto.response.ProductResponse;

import com.se2.demo.dto.request.ProductFilterRequest;
import com.se2.demo.dto.response.PageResponse;

public interface ProductService {
    PageResponse<ProductResponse> getAllProducts(ProductFilterRequest filterRequest);

    ProductResponse getProductById(Integer id);

    ProductResponse getProductBySlug(String slug);

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Integer id, ProductRequest request);

    void deleteProduct(Integer id);
}
