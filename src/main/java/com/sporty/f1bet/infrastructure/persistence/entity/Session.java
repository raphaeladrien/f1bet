package com.sporty.f1bet.infrastructure.persistence.entity;

import java.util.Arrays;

public record Session(
        Long id,
        Integer sessionKey,
        String name,
        String country,
        String countryName,
        String sessionName,
        SessionType sessionType) {
    public enum SessionType {
        PRACTICE,
        QUALIFYING,
        RACE,
        SPRINT;

        public static SessionType fromString(String value) {
            return Arrays.stream(SessionType.values())
                    .filter(e -> e.name().equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid SessionType: " + value));
        }
    }
}
