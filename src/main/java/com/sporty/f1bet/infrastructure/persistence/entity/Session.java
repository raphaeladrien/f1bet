package com.sporty.f1bet.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_key", nullable = false, unique = true)
    private Integer sessionKey;

    @Column
    private String name;

    @Column(name = "session_year")
    private Integer year;

    @Column
    private String country;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column(name = "session_name")
    private String sessionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Driver> drivers = new ArrayList<>();

    protected Session() {}

    public Session(
            Integer sessionKey,
            String name,
            Integer year,
            String country,
            String countryName,
            String sessionName,
            SessionType sessionType,
            List<Driver> drivers) {
        this.id = null;
        this.sessionKey = sessionKey;
        this.name = name;
        this.year = year;
        this.country = country;
        this.countryName = countryName;
        this.sessionName = sessionName;
        this.sessionType = sessionType;
        this.drivers = drivers != null ? new ArrayList<>(drivers) : new ArrayList<>();
    }

    public enum SessionType {
        PRACTICE,
        QUALIFYING,
        RACE,
        SPRINT;

        public static SessionType fromString(String value) {
            if (value == null) return null;

            return Arrays.stream(SessionType.values())
                    .filter(e -> e.name().equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid SessionType: " + value));
        }
    }

    public Long getId() {
        return id;
    }

    public Integer getSessionKey() {
        return sessionKey;
    }

    public String getName() {
        return name;
    }

    public Integer getYear() {
        return year;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getSessionName() {
        return sessionName;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSessionKey(Integer sessionKey) {
        this.sessionKey = sessionKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers != null ? new ArrayList<>(drivers) : new ArrayList<>();
    }

    public Boolean addDriver(Driver driver) {
        return getDrivers().add(driver);
    }
}
