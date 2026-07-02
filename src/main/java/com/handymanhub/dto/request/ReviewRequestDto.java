package com.handymanhub.dto.request;

import jakarta.validation.constraints.*;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// Frontend sends ONLY the booking ID and rating.
// Customer, worker, contractor are all extracted FROM the booking
// by the service — the reviewer doesn't choose them.
//
// This prevents a malicious user from reviewing a worker they
// never booked with. The booking itself determines who gets reviewed.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

public class ReviewRequestDto {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Size(max = 1000, message = "Comment must be under 1000 characters")
    private String comment;

    public Long getBookingId()              { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Integer getRating()              { return rating; }
    public void setRating(Integer rating)   { this.rating = rating; }

    public String getComment()              { return comment; }
    public void setComment(String comment)  { this.comment = comment; }
}