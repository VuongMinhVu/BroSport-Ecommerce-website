package com.se2.demo.service.impl;

import com.se2.demo.dto.async.ProductEvent;
import com.se2.demo.mapper.ProductMapper;
import com.se2.demo.repository.ProductDocumentRepository;
import com.se2.demo.repository.ProductRepository;
import com.se2.demo.service.ProductDocumentAsyncService;
import com.se2.demo.utils.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class ProductDocumentAsyncServiceImpl implements ProductDocumentAsyncService {

  private final ProductDocumentRepository productDocumentRepository;
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  @Override
  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true, condition = "#productEvent.eventType.equals('PRODUCT_CREATED')", classes = ProductEvent.class)
  public void createProduct(ProductEvent productEvent) {
    if(ObjectUtils.isEmpty(productEvent.getProductDocument())) throw new ResourceNotFoundException("Product document is empty");
    productDocumentRepository.save(productEvent.getProductDocument());
  }

  @Override
  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true, condition = "#productEvent.eventType.equals('PRODUCT_UPDATED')", classes = ProductEvent.class)
  public void updateProduct(ProductEvent productEvent) {
    if(ObjectUtils.isEmpty(productEvent.getProductDocument())) throw new ResourceNotFoundException("Product document is empty");
    productDocumentRepository.save(productEvent.getProductDocument());

  }

  @Override
  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true, condition = "#productEvent.eventType.equals('PRODUCT_DELETED')", classes = ProductEvent.class)
  public void deleteProduct(ProductEvent productEvent) {
    if(ObjectUtils.isEmpty(productEvent.getProductDocument())) throw new ResourceNotFoundException("Product document is empty");
    productDocumentRepository.delete(productEvent.getProductDocument());
  }
}
