CREATE TABLE bets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance DECIMAL(19, 2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    session_key INT NOT NULL,
    driver_number INT NOT NULL,
    odd INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_bet_user FOREIGN KEY (user_id) REFERENCES users(id)
);
