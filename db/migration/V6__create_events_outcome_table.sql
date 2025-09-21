CREATE TABLE if not exists events_outcome (
    id BIGINT PRIMARY KEY,
    session_key INT NOT NULL,
    winning_driver_number INT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_events_sessions_key ON events_outcome(session_key);
