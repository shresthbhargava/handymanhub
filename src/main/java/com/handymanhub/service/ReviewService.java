package com.handymanhub.service;

import com.handymanhub.dto.request.ReviewRequestDto;
import com.handymanhub.dto.response.ReviewResponseDto;
import com.handymanhub.exception.ResourceNotFoundException;
import com.handymanhub.model.Booking;
import com.handymanhub.model.Booking.Status;
import com.handymanhub.model.Review;
import com.handymanhub.repository.BookingRepository;
import com.handymanhub.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// BUSINESS RULES — each rule has a reason:
//
// Rule 1: Booking must exist.
//   → Prevents reviewing a non-existent booking (or sending a fake ID).
//
// Rule 2: Booking must be COMPLETED.
//   → You can't review a service that hasn't happened yet.
//   → You can't review a cancelled service.
//   → This is the same rule Urban Company and Uber use.
//
// Rule 3: One review per booking (enforced at DB level too).
//   → Prevents a customer from spamming 5-star reviews on one booking
//     or revenge-downvoting multiple times.
//   → We check in Java AND have a UNIQUE constraint on booking_id.
//     Java check = friendly error message.
//     DB constraint = last line of defense (in case of race condition).
//
// Rule 4: customer_id, worker_id, contractor_id come FROM the booking.
//   → The frontend doesn't send these. The service extracts them.
//   → This means a customer can ONLY review the worker/contractor
//     from their own booking. They can't review a random worker.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ReviewResponseDto create(ReviewRequestDto dto) {
        log.info("Creating review for booking id={} rating={}", dto.getBookingId(), dto.getRating());

        // Rule 1: Booking must exist
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", dto.getBookingId()));

        // Rule 2: Booking must be COMPLETED
        if (booking.getStatus() != Status.COMPLETED) {
            throw new IllegalArgumentException(
                    "Can only review COMPLETED bookings. This booking is: " + booking.getStatus());
        }

        // Rule 3: One review per booking
        if (reviewRepository.findByBookingId(dto.getBookingId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Review already exists for booking id=" + dto.getBookingId());
        }

        // Rule 4: Build review from booking data (not from request)
        Review review = Review.builder()
                .booking(booking)
                .customer(booking.getCustomer())
                .worker(booking.getWorker())
                .contractor(booking.getContractor())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        log.info("Review created id={} for booking id={}", saved.getId(), booking.getId());

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getByWorker(Long workerId, Pageable pageable) {
        return reviewRepository.findByWorkerId(workerId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long workerId) {
        return reviewRepository.averageRatingByWorkerId(workerId);
    }

    @Transactional(readOnly = true)
    public long getReviewCount(Long workerId) {
        return reviewRepository.countByWorkerId(workerId);
    }

    // ── DTO mapping ─────────────────────────────────────────────
    private ReviewResponseDto toDto(Review r) {
        ReviewResponseDto.Builder b = ReviewResponseDto.builder()
                .id(r.getId())
                .bookingId(r.getBooking().getId())
                .customerId(r.getCustomer().getId())
                .customerName(r.getCustomer().getName())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt());

        // Worker OR contractor — one will be null
        if (r.getWorker() != null) {
            b.workerId(r.getWorker().getId())
             .workerName(r.getWorker().getName());
        }
        if (r.getContractor() != null) {
            b.contractorId(r.getContractor().getId())
             .contractorName(r.getContractor().getName());
        }

        return b.build();
    }
}