-- ══════════════════════════════════════════
--  HandymanHub — V2: contractors table
-- ══════════════════════════════════════════

CREATE TABLE contractors (
                             id           BIGINT          NOT NULL AUTO_INCREMENT,
                             name         VARCHAR(100)    NOT NULL,
                             phone        VARCHAR(15)     NOT NULL,
                             email        VARCHAR(150),
                             pincode      VARCHAR(10),
                             company_name VARCHAR(150),
                             verified     TINYINT(1)      NOT NULL DEFAULT 0,
                             created_at   DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                             PRIMARY KEY (id),
                             UNIQUE KEY uq_contractors_phone (phone),
                             UNIQUE KEY uq_contractors_email (email),
                             INDEX idx_contractors_pincode   (pincode),
                             INDEX idx_contractors_verified  (verified)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;