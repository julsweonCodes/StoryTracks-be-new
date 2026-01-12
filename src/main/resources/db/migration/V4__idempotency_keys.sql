-- Idempotency key table for DB-based idempotency
CREATE TABLE idempotency_keys (
    id BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(128) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    endpoint VARCHAR(128) NOT NULL,
    request_hash VARCHAR(128),
    response TEXT,
    status_code INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    UNIQUE (idempotency_key, user_id, endpoint)
);
CREATE INDEX idx_idempotency_keys_expires_at ON idempotency_keys (expires_at);
