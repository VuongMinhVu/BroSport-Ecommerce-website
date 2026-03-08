package com.se2.demo.repository;

import com.se2.demo.model.entity.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, Long> {
}
