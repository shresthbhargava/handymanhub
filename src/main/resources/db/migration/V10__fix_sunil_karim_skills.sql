-- ══════════════════════════════════════════════════
--  HandymanHub — V10: ensure Sunil and Karim skills
--  Use INSERT IGNORE — safe to run even if already exists
-- ══════════════════════════════════════════════════

INSERT IGNORE INTO worker_skills (worker_id, skill_id, experience_years) VALUES
(5, 1, 7),   -- Sunil Bijlee — Electrician, 7 years
(5, 2, 3),   -- Sunil Bijlee — AC Technician, 3 years
(6, 3, 9);   -- Karim Plumber — Plumber, 9 years