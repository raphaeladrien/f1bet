package com.sporty.f1bet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bets")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BetStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_key", nullable = false)
    private Integer sessionKey;

    @Column(name = "driver_number", nullable = false)
    private Integer driverNumber;

    @Column
    private Integer odd;

    public Bet() {}

    public Bet(BigDecimal balance, BetStatus status, User user, Integer sessionKey, Integer driverNumber, Integer odd) {
        this.balance = balance;
        this.status = status;
        this.user = user;
        this.sessionKey = sessionKey;
        this.driverNumber = driverNumber;
        this.odd = odd;
    }

    public enum BetStatus {
        PENDING,
        WON,
        LOST
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BetStatus getStatus() {
        return status;
    }

    public void setStatus(BetStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(Integer sessionKey) {
        this.sessionKey = sessionKey;
    }

    public Integer getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(Integer driverNumber) {
        this.driverNumber = driverNumber;
    }

    public Integer getOdd() {
        return odd;
    }

    public void setOdd(Integer odd) {
        this.odd = odd;
    }
}
