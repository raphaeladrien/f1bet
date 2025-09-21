CREATE TABLE if not exists bets (
    id UUID default UUID(),
    balance DECIMAL(19, 2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    session_key INT NOT NULL,
    driver_number INT NOT NULL,
    odd INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_bet_user FOREIGN KEY (user_id) REFERENCES users(id)
);
