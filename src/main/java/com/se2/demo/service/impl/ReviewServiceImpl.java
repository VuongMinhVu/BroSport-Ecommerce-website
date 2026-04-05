package com.se2.demo.service.impl;

import com.se2.demo.dto.request.ReviewRequest;
import com.se2.demo.dto.response.ReviewListResponse;
import com.se2.demo.dto.response.ReviewResponse;
import com.se2.demo.model.entity.Product;
import com.se2.demo.model.entity.Review;
import com.se2.demo.model.entity.User;
import com.se2.demo.repository.OrderRepository;
import com.se2.demo.repository.ProductRepository;
import com.se2.demo.repository.ReviewRepository;
import com.se2.demo.repository.UserRepository;
import com.se2.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public ReviewResponse createReview(Integer userId, ReviewRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Đánh giá phải từ 1 đến 5 sao");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        Review parentReview = null;
        if (request.getParentReviewId() != null && request.getParentReviewId() > 0) {
            parentReview = reviewRepository.findById(request.getParentReviewId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận gốc"));
            
            if (user.getRole() == null || !user.getRole().equalsIgnoreCase("ADMIN")) {
                throw new RuntimeException("Chỉ Admin mới có quyền trả lời bình luận");
            }
            if (parentReview.getReplies() != null && !parentReview.getReplies().isEmpty()) {
                throw new RuntimeException("Bình luận này đã được Admin trả lời");
            }
        } else {
            boolean hasPurchased = orderRepository.hasUserPurchasedProduct(userId, request.getProductId());
            if (!hasPurchased) {
                throw new RuntimeException("Bạn phải mua sản phẩm này mới có thể đánh giá");
            }
            boolean alreadyReviewed = reviewRepository.existsByUserIdAndProductIdAndParentReviewIsNull(userId, request.getProductId());
            if (alreadyReviewed) {
                throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi");
            }
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .parentReview(parentReview)
                .build();

        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    @Override
    public ReviewListResponse getReviewsByProduct(Integer productId, Integer rating, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewRepository.findByProductIdAndRatingAndParentReviewIsNull(productId, rating, pageRequest);

        Page<ReviewResponse> responsePage = reviewPage.map(this::mapToResponse);

        Double averageRating = reviewRepository.calculateAverageRating(productId);
        Long totalReviews = reviewRepository.countByProductIdAndParentReviewIsNull(productId);
        
        Map<Integer, Long> ratingCounts = new java.util.HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingCounts.put(i, 0L);
        }
        
        List<Object[]> counts = reviewRepository.countReviewsByRating(productId);
        for (Object[] count : counts) {
            if (count[0] != null && count[1] != null) {
                ratingCounts.put(((Number) count[0]).intValue(), ((Number) count[1]).longValue());
            }
        }

        return ReviewListResponse.builder()
                .reviews(responsePage)
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .ratingCounts(ratingCounts)
                .build();
    }

    private ReviewResponse mapToResponse(Review review) {
        List<ReviewResponse> replyResponses = null;
        if (review.getReplies() != null && !review.getReplies().isEmpty()) {
            replyResponses = review.getReplies().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .fullName(review.getUser().getFullName())
                .avatarUrl(review.getUser().getAvatarUrl())
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .replies(replyResponses)
                .build();
    }
}
