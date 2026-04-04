package com.se2.demo.controller;

import com.se2.demo.dto.request.ProductCriteriaRequest;
import com.se2.demo.dto.response.PageResponse;
import com.se2.demo.dto.response.ProductDocumentResponse;
import com.se2.demo.dto.response.ProductNameResponse;
import com.se2.demo.service.ProductDocumentSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class ProductSearchController {

  private final ProductDocumentSearchService searchService;


  @GetMapping("/autocomplete")
  public ResponseEntity<List<ProductNameResponse>> autocomplete(
          @RequestParam(value = "keyword", defaultValue = "") String keyword) {

    if (keyword.trim().isEmpty()) {
      return ResponseEntity.ok(List.of());
    }

    return ResponseEntity.ok(searchService.autoCompleteProductName(keyword));
  }

  @GetMapping("/products")
  public ResponseEntity<PageResponse<ProductDocumentResponse>> searchAndFilterProducts(
          @ModelAttribute ProductCriteriaRequest criteriaRequest) {
    PageResponse<ProductDocumentResponse> result = searchService.searchProduct(criteriaRequest);
    return ResponseEntity.ok(result);
  }


}