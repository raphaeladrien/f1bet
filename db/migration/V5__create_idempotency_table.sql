CREATE TABLE if not exists idempotency_keys (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    result_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
