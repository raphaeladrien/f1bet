package com.sporty.f1bet.application.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "driver_number")
    private Integer driverNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    protected Driver() {}

    public Driver(String fullName, Integer driverNumber, Session session) {
        this.id = null;
        this.fullName = fullName;
        this.driverNumber = driverNumber;
        this.session = session;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getDriverNumber() {
        return driverNumber;
    }

    public Session getSession() {
        return session;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDriverNumber(Integer driverNumber) {
        this.driverNumber = driverNumber;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return Objects.equals(id, driver.id)
                && Objects.equals(fullName, driver.fullName)
                && Objects.equals(driverNumber, driver.driverNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, driverNumber);
    }
}
