-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- Pass 2a: Reviews Table
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
--
-- WHY THIS TABLE EXISTS:
--   Customers need to rate workers after a COMPLETED booking.
--   This is the #1 feature every service marketplace has (Uber, Swiggy,
--   Urban Company all have it). Without reviews, there's no trust signal.
--
-- KEY DESIGN DECISIONS:
--
-- 1. booking_id is UNIQUE — one review per booking.
--    A customer can't review the same booking twice.
--    The database enforces this, not just application code.
--
-- 2. customer_id, worker_id, contractor_id are NOT nullable in the
--    real scenario, but we make worker_id and contractor_id nullable
--    because a booking has EITHER a worker OR a contractor, not both.
--    The service layer ensures the correct one is set.
--
-- 3. rating has a CHECK constraint — database-level validation.
--    Even if a bug in your Java code sends rating=0 or rating=6,
--    the database rejects it. Defense in depth.
--
-- 4. No index on rating — nobody queries "give me all 4-star reviews".
--    The common queries are:
--      - Reviews for a specific worker → indexed by worker_id
--      - Reviews for a specific booking → indexed by booking_id (UNIQUE)
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

CREATE TABLE reviews (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- The booking being reviewed. UNIQUE = one review per booking.
    booking_id     BIGINT NOT NULL,

    -- Who wrote the review (the customer from the booking)
    customer_id    BIGINT NOT NULL,

    -- Who is being reviewed (the worker OR contractor from the booking)
    worker_id      BIGINT,
    contractor_id  BIGINT,

    -- 1-5 stars, enforced by CHECK constraint at database level
    rating         INT NOT NULL CHECK (rating BETWEEN 1 AND 5),

    -- Optional text review
    comment        TEXT,

    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT uk_review_booking     UNIQUE (booking_id),
    CONSTRAINT fk_review_booking     FOREIGN KEY (booking_id)    REFERENCES bookings(id),
    CONSTRAINT fk_review_customer    FOREIGN KEY (customer_id)   REFERENCES customers(id),
    CONSTRAINT fk_review_worker      FOREIGN KEY (worker_id)     REFERENCES workers(id),
    CONSTRAINT fk_review_contractor  FOREIGN KEY (contractor_id) REFERENCES contractors(id)
);

-- Most common query: "get all reviews for worker #5"
CREATE INDEX idx_review_worker_id ON reviews(worker_id);

-- Second most common: "get all reviews for contractor #3"
CREATE INDEX idx_review_contractor_id ON reviews(contractor_id);