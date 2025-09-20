package com.sporty.f1bet.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionResponse {
    private final UUID id;
    private final String type;
    private final String name;
    private final String country;
    private final String circuit;

    private final List<DriverResponse> drivers;

    public SessionResponse(String type, String name, String country, String circuit) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.country = country;
        this.circuit = circuit;
        this.drivers = new ArrayList<>(20);
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<DriverResponse> getDrivers() {
        return drivers;
    }

    public String getCountry() {
        return country;
    }

    public UUID getId() {
        return id;
    }

    public String getCircuit() {
        return circuit;
    }

    public void addDriver(final DriverResponse driver) {
        drivers.add(driver);
    }
}
