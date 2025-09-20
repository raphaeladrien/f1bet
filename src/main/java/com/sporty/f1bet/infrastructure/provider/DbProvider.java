package com.sporty.f1bet.infrastructure.provider;

import com.sporty.f1bet.application.provider.Provider;
import com.sporty.f1bet.infrastructure.persistence.entity.Driver;
import com.sporty.f1bet.infrastructure.persistence.entity.Session;
import com.sporty.f1bet.infrastructure.persistence.repository.SessionRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DbProvider implements Provider {

    private final SessionRepository sessionRepository;

    public DbProvider(final SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<Session> getSessions() {
        sessionRepository.findAll();
        return List.of();
    }

    @Override
    public List<Driver> getDrivers(Integer sessionKey) {
        return List.of();
    }
}
