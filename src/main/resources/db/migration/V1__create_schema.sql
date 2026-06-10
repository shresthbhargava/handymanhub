-- ══════════════════════════════════════════
--  HandymanHub — V1: skills table
-- ══════════════════════════════════════════

CREATE TABLE skills (
                        id          BIGINT          NOT NULL AUTO_INCREMENT,
                        name        VARCHAR(100)    NOT NULL,
                        category    VARCHAR(100),
                        description TEXT,

                        PRIMARY KEY (id),
                        UNIQUE KEY uq_skills_name (name),
                        INDEX idx_skills_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;