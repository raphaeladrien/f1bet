CREATE TABLE idempotency_keys (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    bet_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
