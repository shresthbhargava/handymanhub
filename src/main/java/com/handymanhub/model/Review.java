package com.handymanhub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// WHY THESE RELATIONSHIPS:
//
// Booking → Review is ONE-TO-ONE (enforced by UNIQUE on booking_id).
// We use @ManyToOne because JPA doesn't have @OneToOne with
// "the child owns the foreign key" cleanly. @ManyToOne with
// UNIQUE constraint on the FK column achieves the same result.
//
// Worker/Customer/Contractor are @ManyToOne (many reviews per worker).
// We use LAZY fetching because we don't always need the full
// worker/customer object when loading a review list.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Entity
@Table(name = "reviews",
       uniqueConstraints = @UniqueConstraint(name = "uk_review_booking", columnNames = "booking_id"))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One booking = one review (UNIQUE constraint in DB + here)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Nullable because a booking has EITHER worker OR contractor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id")
    private Contractor contractor;

    @Column(nullable = false)
    @Min(1) @Max(5)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Review() {}

    // ── Getters and Setters ─────────────────────────────────────
    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public Booking getBooking()            { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Customer getCustomer()          { return customer; }
    public void setCustomer(Customer c)    { this.customer = c; }

    public Worker getWorker()              { return worker; }
    public void setWorker(Worker w)        { this.worker = w; }

    public Contractor getContractor()      { return contractor; }
    public void setContractor(Contractor c){ this.contractor = c; }

    public Integer getRating()             { return rating; }
    public void setRating(Integer rating)  { this.rating = rating; }

    public String getComment()             { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt()    { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Booking booking;
        private Customer customer;
        private Worker worker;
        private Contractor contractor;
        private Integer rating;
        private String comment;

        public Builder booking(Booking v)       { this.booking = v; return this; }
        public Builder customer(Customer v)     { this.customer = v; return this; }
        public Builder worker(Worker v)         { this.worker = v; return this; }
        public Builder contractor(Contractor v) { this.contractor = v; return this; }
        public Builder rating(Integer v)        { this.rating = v; return this; }
        public Builder comment(String v)        { this.comment = v; return this; }

        public Review build() {
            Review r = new Review();
            r.booking = this.booking;
            r.customer = this.customer;
            r.worker = this.worker;
            r.contractor = this.contractor;
            r.rating = this.rating;
            r.comment = this.comment;
            return r;
        }
    }
}