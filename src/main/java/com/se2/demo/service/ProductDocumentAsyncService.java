package com.se2.demo.service;

import com.se2.demo.dto.async.ProductEvent;

public interface ProductDocumentAsyncService {
  void createProduct(ProductEvent productEvent);
  void updateProduct(ProductEvent productEvent);
  void deleteProduct(ProductEvent productEvent);
}
