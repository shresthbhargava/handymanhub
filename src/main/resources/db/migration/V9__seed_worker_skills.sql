-- ══════════════════════════════════════════
--  HandymanHub — V9: seed worker skills
-- ══════════════════════════════════════════

-- Worker IDs from V7 seed:
-- 1=Suresh Chauhan, 2=Dinesh Rajput, 3=Raju Prasad
-- 4=Gurdeep Sandhu, 5=Sunil Bijlee, 6=Karim Plumber, 7=Geeta Bai

-- Skill IDs from V7 seed:
-- 1=Electrician, 2=AC Technician, 3=Plumber, 4=Waterproofing
-- 5=Mason, 6=Tile Worker, 7=Painter, 8=Carpenter, 9=Maid, 10=Cook

INSERT INTO worker_skills (worker_id, skill_id, experience_years) VALUES
-- Suresh Chauhan — Ramesh's crew, civil work
(1, 5, 5),   -- Mason, 5 years
(1, 6, 3),   -- Tile Worker, 3 years

-- Dinesh Rajput — Ramesh's crew
(2, 5, 4),   -- Mason, 4 years
(2, 7, 2),   -- Painter, 2 years

-- Raju Prasad — Santosh's crew
(3, 7, 3),   -- Painter, 3 years
(3, 8, 2),   -- Carpenter, 2 years

-- Gurdeep Sandhu — Harpreet's crew
(4, 8, 8),   -- Carpenter, 8 years
(4, 6, 4),   -- Tile Worker, 4 years

-- Sunil Bijlee — independent electrician
-- already assigned via API, skip to avoid duplicate
-- (5, 1, 7) already exists

-- Karim Plumber — independent
-- already assigned via API, skip
-- (6, 3, 9) already exists

-- Karim also does waterproofing
(6, 4, 4),   -- Waterproofing, 4 years

-- Geeta Bai — domestic help
(7, 9, 6),   -- Maid, 6 years
(7, 10, 3);  -- Cook, 3 years