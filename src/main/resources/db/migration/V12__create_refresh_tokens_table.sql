-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- Pass 1a: Refresh Token Table
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
--
-- WHY THIS TABLE EXISTS:
--   Right now your JWT is valid for 24 hours. If stolen, hacker has 24hr access.
--   Refresh tokens fix this by making access tokens short-lived (15 min).
--   This table stores the refresh tokens so we can:
--     1. ROTATE them (old one becomes invalid after use)
--     2. REVOKE them (on logout)
--     3. CHECK expiry (auto-cleanup of old tokens)
--
-- KEY DESIGN DECISION — We store the HASH, not the raw token:
--   If someone gets database access (SQL injection, backup leak, etc.),
--   they see only SHA-256 hashes — completely useless without the raw token.
--   Same principle as why we hash passwords, not store them plain.
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

CREATE TABLE refresh_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- SHA-256 hash of the actual UUID token (never store the raw token!)
    token_hash  VARCHAR(64) NOT NULL,

    -- Which user owns this token
    user_id     BIGINT NOT NULL,

    -- When this token becomes invalid
    expires_at  DATETIME NOT NULL,

    -- For auditing
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,

    -- True if the user logged out or we detected suspicious activity
    revoked     BOOLEAN NOT NULL DEFAULT FALSE,

    -- Constraints
    CONSTRAINT uk_refresh_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_token_user  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Index: fast lookup by user (so we can find all tokens for a user)
-- Also useful if we want "logout from all devices" in the future
CREATE INDEX idx_refresh_token_user_id ON refresh_tokens(user_id);

-- Index: cleanup expired tokens (a scheduled job could DELETE WHERE expires_at < NOW())
CREATE INDEX idx_refresh_token_expires  ON refresh_tokens(expires_at);