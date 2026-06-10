-- ══════════════════════════════════════════
--  HandymanHub — V8: worker_skills table
-- ══════════════════════════════════════════

CREATE TABLE worker_skills (
                               id               BIGINT  NOT NULL AUTO_INCREMENT,
                               worker_id        BIGINT  NOT NULL,
                               skill_id         BIGINT  NOT NULL,
                               experience_years INT,

                               PRIMARY KEY (id),
                               UNIQUE KEY uq_worker_skill        (worker_id, skill_id),
                               INDEX idx_worker_skills_skill_id  (skill_id),

                               CONSTRAINT fk_ws_worker
                                   FOREIGN KEY (worker_id)
                                       REFERENCES workers (id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_ws_skill
                                   FOREIGN KEY (skill_id)
                                       REFERENCES skills (id)
                                       ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;