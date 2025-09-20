package com.sporty.f1bet.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "sessions")
public record Session(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id,
        @Column(name = "session_key", nullable = false, unique = true) Integer sessionKey,
        @Column String name,
        @Column(name = "session_year") Integer year,
        @Column String country,
        @Column(name = "country_name", nullable = false) String countryName,
        @Column(name = "session_name") String sessionName,
        @Enumerated(EnumType.STRING) @Column(name = "session_type", nullable = false) SessionType sessionType,
        @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true) List<Driver> drivers) {
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
