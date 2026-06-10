-- ══════════════════════════════════════════
--  HandymanHub — V6: bookings table
-- ══════════════════════════════════════════

CREATE TABLE bookings (
                          id             BIGINT       NOT NULL AUTO_INCREMENT,
                          customer_id    BIGINT       NOT NULL,
                          worker_id      BIGINT,
                          contractor_id  BIGINT,
                          skill_id       BIGINT       NOT NULL,
                          scheduled_date DATE         NOT NULL,
                          duration_days  INT          NOT NULL DEFAULT 1,
                          status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                          address        TEXT,
                          notes          TEXT,
                          created_at     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                          PRIMARY KEY (id),
                          INDEX idx_bookings_customer_id   (customer_id),
                          INDEX idx_bookings_worker_id     (worker_id),
                          INDEX idx_bookings_contractor_id (contractor_id),
                          INDEX idx_bookings_status        (status),
                          INDEX idx_bookings_date          (scheduled_date),

                          CONSTRAINT fk_bookings_customer
                              FOREIGN KEY (customer_id)
                                  REFERENCES customers (id)
                                  ON DELETE RESTRICT,

                          CONSTRAINT fk_bookings_worker
                              FOREIGN KEY (worker_id)
                                  REFERENCES workers (id)
                                  ON DELETE SET NULL,

                          CONSTRAINT fk_bookings_contractor
                              FOREIGN KEY (contractor_id)
                                  REFERENCES contractors (id)
                                  ON DELETE SET NULL,

                          CONSTRAINT fk_bookings_skill
                              FOREIGN KEY (skill_id)
                                  REFERENCES skills (id)
                                  ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;