CREATE TABLE if not exists users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance DECIMAL(19, 2) NOT NULL,
    version BIGINT NOT NULL DEFAULT 1,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
