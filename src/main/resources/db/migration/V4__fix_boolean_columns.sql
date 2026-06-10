-- ══════════════════════════════════════════════════
--  HandymanHub — V4: fix boolean column definitions
-- ══════════════════════════════════════════════════

ALTER TABLE contractors
    MODIFY COLUMN verified BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE workers
    MODIFY COLUMN available BOOLEAN NOT NULL DEFAULT TRUE;