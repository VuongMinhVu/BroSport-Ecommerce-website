package com.se2.demo.service.impl;

import com.se2.demo.dto.request.ProductRequest;
import com.se2.demo.dto.response.ProductResponse;
import com.se2.demo.mapper.ProductMapper;
import com.se2.demo.model.entity.*;
import com.se2.demo.repository.*;
import com.se2.demo.service.ProductService;
import com.se2.demo.utils.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TargetCustomerRepository targetCustomerRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productMapper.toProductResponseList(productRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
         return null;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        product.setCategory(category);

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId()));
        product.setBrand(brand);

        if (request.getTargetCustomerIds() != null && !request.getTargetCustomerIds().isEmpty()) {
            Set<TargetCustomer> targetCustomers = new HashSet<>(
                    targetCustomerRepository.findAllById(request.getTargetCustomerIds()));
            product.setTargetCustomers(targetCustomers);
        }

        if (request.getProductDetails() != null) {
            List<ProductDetail> details = request.getProductDetails().stream().map(detailReq -> {
                ProductDetail pd = new ProductDetail();
                pd.setProduct(product);
                pd.setPrice(detailReq.getPrice());
                pd.setCompareAtPrice(detailReq.getCompareAtPrice());
                pd.setStockQuantity(detailReq.getStockQuantity());
                pd.setWeightGrams(detailReq.getWeightGrams());
                pd.setSku(detailReq.getSku());

                Color color = colorRepository.findById(detailReq.getColorId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Color not found with id: " + detailReq.getColorId()));
                pd.setColor(color);

                Size size = sizeRepository.findById(detailReq.getSizeId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Size not found with id: " + detailReq.getSizeId()));
                pd.setSize(size);

                if (detailReq.getVariantImages() != null) {
                    List<ProductImage> variantImages = detailReq.getVariantImages().stream().map(imgReq -> {
                        ProductImage img = new ProductImage();
                        img.setProduct(product);
                        img.setVariant(pd);
                        img.setImageUrl(imgReq.getImageUrl());
                        img.setIsMain(imgReq.getIsMain());
                        img.setSortOrder(imgReq.getSortOrder());
                        return img;
                    }).collect(Collectors.toList());
                    pd.setVariantImages(variantImages);
                }

                return pd;
            }).collect(Collectors.toList());
            product.setProductDetails(details);
        }

        if (request.getProductImages() != null) {
            List<ProductImage> mainImages = request.getProductImages().stream().map(imgReq -> {
                ProductImage img = new ProductImage();
                img.setProduct(product);
                img.setImageUrl(imgReq.getImageUrl());
                img.setIsMain(imgReq.getIsMain());
                img.setSortOrder(imgReq.getSortOrder());
                return img;
            }).collect(Collectors.toList());
            product.setProductImages(mainImages);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Integer id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        existingProduct.setName(request.getName());
        existingProduct.setSlug(request.getSlug());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setStatus(request.getStatus());

        return productMapper.toResponse(productRepository.save(existingProduct));
    }

    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(existingProduct);
    }
}
