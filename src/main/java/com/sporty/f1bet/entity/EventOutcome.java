package com.sporty.f1bet.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "events_outcome")
public class EventOutcome extends Auditable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "session_key", nullable = false, unique = true)
    private Integer sessionKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "winning_driver_number", nullable = false, unique = true)
    private Integer winningDriverNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    public EventOutcome() {}

    public EventOutcome(Integer sessionKey, Integer winningDriverNumber, User user) {
        this.sessionKey = sessionKey;
        this.winningDriverNumber = winningDriverNumber;
        this.user = user;
        this.status = EventStatus.PENDING;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(Integer sessionKey) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public enum EventStatus {
        PENDING,
        FINISHED
    }
}
