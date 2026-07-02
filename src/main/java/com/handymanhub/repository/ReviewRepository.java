package com.handymanhub.repository;

import com.handymanhub.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// QUERIES AND WHY THEY EXIST:
//
// 1. findByBookingId — check if a review already exists for a booking.
//    Used in service to enforce "one review per booking" rule.
//
// 2. findByWorkerId — "show all reviews for this worker".
//    Used on the worker's profile page.
//
// 3. findByContractorId — same but for contractors.
//
// 4. averageRatingByWorkerId — the most important query for the
//    search results page. When a customer searches for electricians,
//    each worker card shows "4.3 ★ (12 reviews)". This query
//    calculates that average in a single SQL query.
//
//    The SQL it generates:
//    SELECT AVG(r.rating) FROM reviews r WHERE r.worker_id = ?
//
//    COALESCE returns 0.0 if no reviews exist (instead of NULL).
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByBookingId(Long bookingId);

    Page<Review> findByWorkerId(Long workerId, Pageable pageable);

    Page<Review> findByContractorId(Long contractorId, Pageable pageable);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.worker.id = :workerId")
    Double averageRatingByWorkerId(@Param("workerId") Long workerId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.contractor.id = :contractorId")
    Double averageRatingByContractorId(@Param("contractorId") Long contractorId);

    long countByWorkerId(Long workerId);
}