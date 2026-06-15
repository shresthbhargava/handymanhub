-- ══════════════════════════════════════════
--  HandymanHub — V11: users table
-- ══════════════════════════════════════════

CREATE TABLE users (
                       id         BIGINT        NOT NULL AUTO_INCREMENT,
                       name       VARCHAR(100)  NOT NULL,
                       email      VARCHAR(150)  NOT NULL,
                       password   VARCHAR(255)  NOT NULL,
                       role       VARCHAR(20)   NOT NULL DEFAULT 'CUSTOMER',
                       created_at DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                       PRIMARY KEY (id),
                       UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;