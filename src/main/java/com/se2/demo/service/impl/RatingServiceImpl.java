package com.se2.demo.service.impl;

import com.se2.demo.dto.request.RatingRequest;
import com.se2.demo.dto.response.PageResponse;
import com.se2.demo.dto.response.ProductRatingSummaryResponse;
import com.se2.demo.dto.response.RatingResponse;
import com.se2.demo.mapper.RatingMapper;
import com.se2.demo.model.entity.Product;
import com.se2.demo.model.entity.Rating;
import com.se2.demo.repository.ProductRepository;
import com.se2.demo.repository.RatingRepository;
import com.se2.demo.service.RatingService;
import com.se2.demo.utils.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final RatingMapper ratingMapper;

    @Override
    public RatingResponse createRating(Integer productId, Long userId, RatingRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Rating rating = Rating.builder()
                .product(product)
                .userId(userId)
                .ratingStar(request.ratingStar())
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .build();

        Rating saved = ratingRepository.save(rating);
        return ratingMapper.toResponse(saved);
    }

    @Override
    public ProductRatingSummaryResponse getProductRatings(Integer productId, int page, int size) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Rating> ratingPage = ratingRepository.findAllByProductId(productId, pageable);

        PageResponse<RatingResponse> pageResponse = PageResponse.<RatingResponse>builder()
                .content(ratingPage.getContent().stream().map(ratingMapper::toResponse).toList())
                .pageNo(ratingPage.getNumber())
                .pageSize(ratingPage.getSize())
                .totalElements(ratingPage.getTotalElements())
                .totalPages(ratingPage.getTotalPages())
                .last(ratingPage.isLast())
                .build();

        Double avg = ratingRepository.findAvgRatingByProductId(productId);
        Long total = ratingRepository.countByProductId(productId);

        return new ProductRatingSummaryResponse(
                avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0,
                total,
                pageResponse
        );
    }

    @Override
    public void deleteRating(Long ratingId, Long userId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + ratingId));

        if (!rating.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this rating");
        }

        ratingRepository.delete(rating);
    }
}
