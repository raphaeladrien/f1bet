package com.sporty.f1bet.dto;

import java.util.concurrent.ThreadLocalRandom;

public class DriverResponse {
    private final String name;
    private final Integer number;
    private final Integer odd;

    public DriverResponse(final String name, final Integer number) {
        this.name = name;
        this.number = number;
        this.odd = ThreadLocalRandom.current().nextInt(2, 5);
    }

    public String getName() {
        return name;
    }

    public Integer getNumber() {
        return number;
    }

    public Integer getOdd() {
        return odd;
    }
}
