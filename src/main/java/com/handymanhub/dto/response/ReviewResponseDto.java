package com.handymanhub.dto.response;

import java.time.LocalDateTime;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// Immutable response DTO — no setters, only getters.
// The frontend displays this data; it never modifies it.
// Immutable DTOs are a best practice for response objects
// because they prevent accidental modification after creation.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

public class ReviewResponseDto {

    private final Long id;
    private final Long bookingId;
    private final Long customerId;
    private final String customerName;
    private final Long workerId;
    private final String workerName;
    private final Long contractorId;
    private final String contractorName;
    private final Integer rating;
    private final String comment;
    private final LocalDateTime createdAt;

    private ReviewResponseDto(Builder builder) {
        this.id = builder.id;
        this.bookingId = builder.bookingId;
        this.customerId = builder.customerId;
        this.customerName = builder.customerName;
        this.workerId = builder.workerId;
        this.workerName = builder.workerName;
        this.contractorId = builder.contractorId;
        this.contractorName = builder.contractorName;
        this.rating = builder.rating;
        this.comment = builder.comment;
        this.createdAt = builder.createdAt;
    }

    public Long getId()              { return id; }
    public Long getBookingId()       { return bookingId; }
    public Long getCustomerId()      { return customerId; }
    public String getCustomerName()  { return customerName; }
    public Long getWorkerId()        { return workerId; }
    public String getWorkerName()    { return workerName; }
    public Long getContractorId()    { return contractorId; }
    public String getContractorName(){ return contractorName; }
    public Integer getRating()       { return rating; }
    public String getComment()       { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long bookingId;
        private Long customerId;
        private String customerName;
        private Long workerId;
        private String workerName;
        private Long contractorId;
        private String contractorName;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;

        public Builder id(Long v)                { this.id = v; return this; }
        public Builder bookingId(Long v)         { this.bookingId = v; return this; }
        public Builder customerId(Long v)        { this.customerId = v; return this; }
        public Builder customerName(String v)    { this.customerName = v; return this; }
        public Builder workerId(Long v)          { this.workerId = v; return this; }
        public Builder workerName(String v)      { this.workerName = v; return this; }
        public Builder contractorId(Long v)      { this.contractorId = v; return this; }
        public Builder contractorName(String v)  { this.contractorName = v; return this; }
        public Builder rating(Integer v)         { this.rating = v; return this; }
        public Builder comment(String v)         { this.comment = v; return this; }
        public Builder createdAt(LocalDateTime v){ this.createdAt = v; return this; }

        public ReviewResponseDto build() { return new ReviewResponseDto(this); }
    }
}