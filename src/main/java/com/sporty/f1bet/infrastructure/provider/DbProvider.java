package com.sporty.f1bet.infrastructure.provider;

import com.sporty.f1bet.application.provider.Provider;
import com.sporty.f1bet.infrastructure.persistence.entity.Driver;
import com.sporty.f1bet.infrastructure.persistence.entity.Session;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbProvider implements Provider {
    @Override
    public List<Session> getSessions() {
        return List.of();
    }

    @Override
    public List<Driver> getDrivers(Integer sessionKey) {
        return List.of();
    }
}
