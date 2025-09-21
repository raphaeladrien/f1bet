package com.sporty.f1bet.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "events_outcome")
public class EventOutcome extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_key", nullable = false, unique = true)
    private Long sessionKey;

    @Column(name = "winning_driver_number", nullable = false, unique = true)
    private Integer winningDriverNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(Long sessionKey) {
        this.sessionKey = sessionKey;
    }

    public Integer getWinningDriverNumber() {
        return winningDriverNumber;
    }

    public void setWinningDriverNumber(Integer winningDriverNumber) {
        this.winningDriverNumber = winningDriverNumber;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public enum EventStatus {
        PENDING,
        FINISHED
    }
}
