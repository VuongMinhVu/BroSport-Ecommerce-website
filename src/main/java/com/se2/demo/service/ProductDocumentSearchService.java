package com.se2.demo.service;

import com.se2.demo.dto.request.ProductCriteriaRequest;
import com.se2.demo.dto.request.ProductFilterRequest;
import com.se2.demo.dto.response.PageResponse;
import com.se2.demo.dto.response.ProductDocumentResponse;
import com.se2.demo.dto.response.ProductNameResponse;

import java.util.List;

public interface ProductDocumentSearchService {
  PageResponse<ProductDocumentResponse> searchProduct(ProductCriteriaRequest productCriteriaRequest);
  List<ProductNameResponse> autoCompleteProductName (String keyword);
}
