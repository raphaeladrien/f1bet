package com.sporty.f1bet.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "drivers")
public record Driver(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id,
        @Column(name = "full_name") String fullName,
        @Column(name = "driver_number") Integer driverNumber,
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "session_id", nullable = false) Session session) {}
