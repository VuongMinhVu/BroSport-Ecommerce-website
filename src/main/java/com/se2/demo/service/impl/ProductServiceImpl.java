package com.se2.demo.service.impl;

import com.se2.demo.dto.async.ProductEvent;
import com.se2.demo.dto.request.ProductRequest;
import com.se2.demo.dto.response.ProductResponse;
import com.se2.demo.mapper.ProductMapper;
import com.se2.demo.model.entity.*;
import com.se2.demo.repository.*;
import com.se2.demo.service.CloudinaryService;
import com.se2.demo.service.ProductService;
import com.se2.demo.utils.ResourceNotFoundException;
import com.se2.demo.utils.constant.Constant;
import lombok.RequiredArgsConstructor;
import com.se2.demo.dto.request.ProductFilterRequest;
import com.se2.demo.dto.response.PageResponse;
import com.se2.demo.repository.specification.ProductSpecification;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.se2.demo.repository.ProductDetailRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final GenderRepository genderRepository;
    private final SportRepository sportRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;
    private final ApplicationEventPublisher publisher;
    private final ProductDetailRepository productDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(ProductFilterRequest filterRequest) {

        Sort sort = Sort.unsorted();
        if (filterRequest.getSortBy() != null && !filterRequest.getSortBy().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(filterRequest.getSortDir()) ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            String sortBy = filterRequest.getSortBy();

            List<String> allowedSortFields = java.util.Arrays.asList("id", "name", "price", "createdAt", "updatedAt");
            if (!allowedSortFields.contains(sortBy)) {
                sortBy = "id";
            }

            sort = Sort.by(direction, sortBy);
        }

        int page = (filterRequest.getPage() != null) ? filterRequest.getPage() : 0;
        int size = (filterRequest.getSize() != null && filterRequest.getSize() > 0) ? filterRequest.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = ProductSpecification.filterProducts(filterRequest);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<ProductResponse> content = productMapper.toProductResponseList(productPage.getContent());

        return PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNo(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
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
        // Truy vấn dữ liệu thật từ database
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại với slug: " + slug));

        // Chuyển đổi Entity sang DTO để trả về
        return productMapper.toResponse(product);
    }

    // Thêm vào file ProductServiceImpl.java
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductByVariantId(Integer variantId) {
        // Tìm ProductDetail trước để biết nó thuộc về Product nào
        ProductDetail detail = productDetailRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        // Trả về ProductResponse (chứa đủ thông tin tên, ảnh, giá)
        return productMapper.toResponse(detail.getProduct());
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

        if (request.getGenderId() != null) {
            Gender gender = genderRepository.findById(request.getGenderId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Gender not found with id: " + request.getGenderId()));
            product.setGender(gender);
        }

        if (request.getSportId() != null) {
            Sport sport = sportRepository.findById(request.getSportId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Sport not found with id: " + request.getSportId()));
            product.setSport(sport);
        }

        if (request.getProductDetails() != null) {
            List<ProductDetail> details = request.getProductDetails().stream().map(detailReq -> {
                ProductDetail pd = new ProductDetail();
                pd.setProduct(product);
                pd.setStockQuantity(detailReq.getStockQuantity());
                pd.setWeightInGrams(detailReq.getWeightInGrams());
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
                        // if (imgReq.getImage() != null && !imgReq.getImage().isEmpty()) {
                        // img.setImageUrl(cloudinaryService.uploadFile(imgReq.getImage(),
                        // "products/variants"));
                        // }
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

                if (imgReq.getImage() != null && !imgReq.getImage().isEmpty()) {
                    img.setImageUrl(cloudinaryService.uploadFile(imgReq.getImage(), "products"));
                }

                img.setIsMain(imgReq.getIsMain());
                img.setSortOrder(imgReq.getSortOrder());
                return img;
            }).collect(Collectors.toList());
            product.setProductImages(mainImages);
        }

        Product savedProduct = productRepository.save(product);
        this.publisher.publishEvent(
                new ProductEvent(this.productMapper.toDocument(savedProduct), Constant.PRODUCT_CREATED_EVENT));
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
        existingProduct.setFeature(request.getFeature());
        existingProduct.setInformation(request.getInformation());
        existingProduct.setStatus(request.getStatus());
        existingProduct.setOriginPrice(request.getOriginPrice());
        existingProduct.setShowPrice(request.getShowPrice());

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
