-- ══════════════════════════════════════════
--  HandymanHub — V5: customers table
-- ══════════════════════════════════════════

CREATE TABLE customers (
                           id         BIGINT         NOT NULL AUTO_INCREMENT,
                           name       VARCHAR(100)   NOT NULL,
                           phone      VARCHAR(15)    NOT NULL,
                           email      VARCHAR(150)   NOT NULL,
                           address    TEXT,
                           pincode    VARCHAR(10),
                           created_at DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                           PRIMARY KEY (id),
                           UNIQUE KEY uq_customers_phone (phone),
                           UNIQUE KEY uq_customers_email (email),
                           INDEX idx_customers_pincode   (pincode)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;