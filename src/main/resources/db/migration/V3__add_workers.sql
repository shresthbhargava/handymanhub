-- ══════════════════════════════════════════
--  HandymanHub — V3: workers table
-- ══════════════════════════════════════════

CREATE TABLE workers (
                         id            BIGINT         NOT NULL AUTO_INCREMENT,
                         name          VARCHAR(100)   NOT NULL,
                         phone         VARCHAR(15)    NOT NULL,
                         pincode       VARCHAR(10),
                         daily_rate    DECIMAL(10,2),
                         available     TINYINT(1)     NOT NULL DEFAULT 1,
                         created_at    DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                         contractor_id BIGINT,

                         PRIMARY KEY (id),
                         UNIQUE KEY uq_workers_phone      (phone),
                         INDEX idx_workers_pincode        (pincode),
                         INDEX idx_workers_available      (available),
                         INDEX idx_workers_contractor_id  (contractor_id),

                         CONSTRAINT fk_workers_contractor
                             FOREIGN KEY (contractor_id)
                                 REFERENCES contractors (id)
                                 ON DELETE SET NULL
                                 ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;