package com.sporty.f1bet.infrastructure.provider;

import com.sporty.f1bet.application.provider.Provider;
import com.sporty.f1bet.infrastructure.persistence.entity.Driver;
import com.sporty.f1bet.infrastructure.persistence.entity.Session;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenF1Provider implements Provider {

    @Override
    public List<Session> getSessions() {
        System.out.println("Olá");
        return List.of();
    }

    @Override
    public List<Driver> getDrivers(Integer sessionKey) {
        return List.of();
    }
}
