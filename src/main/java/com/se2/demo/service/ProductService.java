package com.se2.demo.service;

import com.se2.demo.dto.request.ProductRequest;
import com.se2.demo.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Integer id);

    ProductResponse getProductBySlug(String slug);

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Integer id, ProductRequest request);

    void deleteProduct(Integer id);
}
