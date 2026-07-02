package com.handymanhub.controller;

import com.handymanhub.dto.request.ReviewRequestDto;
import com.handymanhub.dto.response.ReviewResponseDto;
import com.handymanhub.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// ENDPOINTS:
//
// POST   /api/v1/reviews              — create a review (requires auth)
// GET    /api/v1/reviews/worker/{id}   — public: see worker's reviews
// GET    /api/v1/workers/{id}/rating  — public: get average rating
//
// SECURITY:
// POST is protected (anyRequest().authenticated() in SecurityConfig).
// GET endpoints are public — anyone browsing worker profiles
// should see ratings without logging in.
//
// WHY /workers/{id}/rating IS A SEPARATE ENDPOINT:
// The worker search results page needs to show "4.3 ★" for each
// worker card. Fetching full review objects just for an average
// would be wasteful. This endpoint returns a single number.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Tag(name = "Reviews", description = "Customer reviews and ratings for workers/contractors")
@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(
            summary = "Create a review for a completed booking",
            description = "Only works on COMPLETED bookings. One review per booking. The worker/contractor is determined by the booking, not the request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created"),
            @ApiResponse(responseCode = "400", description = "Booking not completed, or review already exists"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponseDto> createReview(
            @Valid @RequestBody ReviewRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.create(dto));
    }

    @Operation(
            summary = "Get reviews for a worker",
            description = "Public endpoint. Returns paginated reviews for a specific worker."
    )
    @GetMapping("/reviews/worker/{workerId}")
    public ResponseEntity<Page<ReviewResponseDto>> getWorkerReviews(
            @PathVariable Long workerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                reviewService.getByWorker(workerId, PageRequest.of(page, size)));
    }

    @Operation(
            summary = "Get worker's average rating",
            description = "Returns { averageRating: 4.3, totalReviews: 12 }. Used on worker cards in search results."
    )
    @GetMapping("/workers/{workerId}/rating")
    public ResponseEntity<java.util.Map<String, Object>> getWorkerRating(
            @PathVariable Long workerId) {
        Double avg = reviewService.getAverageRating(workerId);
        long count = reviewService.getReviewCount(workerId);
        return ResponseEntity.ok(java.util.Map.of(
                "averageRating", Math.round(avg * 10.0) / 10.0,  // round to 1 decimal
                "totalReviews", count
        ));
    }
}