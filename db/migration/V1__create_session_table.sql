CREATE TABLE if not exists sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_key INT NOT NULL UNIQUE,
    name VARCHAR(255),
    session_year INT,
    country VARCHAR(3),
    country_name VARCHAR(255) NOT NULL,
    session_name VARCHAR(40),
    session_type VARCHAR(40) NOT NULL
);

CREATE INDEX idx_sessions_type_year_country ON sessions(session_type, session_year, country);
CREATE INDEX idx_sessions_type ON sessions(session_type);
CREATE INDEX idx_sessions_key ON sessions(session_year);
CREATE INDEX idx_sessions_country ON sessions(country);


