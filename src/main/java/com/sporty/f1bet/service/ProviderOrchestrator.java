package com.sporty.f1bet.service;

import com.sporty.f1bet.config.ProviderProperties;
import com.sporty.f1bet.entity.Driver;
import com.sporty.f1bet.entity.Session;
import com.sporty.f1bet.provider.Provider;
import com.sporty.f1bet.provider.ProviderFactory;
import com.sporty.f1bet.repository.SessionRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProviderOrchestrator {

    private final ProviderFactory factory;
    private final ProviderProperties providerProperties;
    private final SessionRepository sessionRepository;

    public ProviderOrchestrator(
            final ProviderFactory factory,
            final ProviderProperties providerProperties,
            final SessionRepository sessionRepository) {
        this.factory = factory;
        this.providerProperties = providerProperties;
        this.sessionRepository = sessionRepository;
    }

    public List<Session> getSessions(final String sessionType, final Integer year, final String country) {
        final Integer validatedYear = (year != null && year > 0) ? year : null;

        Provider primaryProvider = factory.getProvider(providerProperties.getPrimary());
        List<Session> primarySessions = primaryProvider.getSessions(sessionType, validatedYear, country);

        if (!primarySessions.isEmpty()) {
            return primarySessions;
        }

        Provider fallbackProvider = factory.getProvider(providerProperties.getFallback());
        List<Session> fallbackSessions = fallbackProvider.getSessions(sessionType, validatedYear, country);

        for (Session session : fallbackSessions) {
            List<Driver> drivers = fallbackProvider.getDrivers(session.getSessionKey());
            for (Driver driver : drivers) {
                driver.setSession(session);
            }
            session.setDrivers(drivers);
        }

        return sessionRepository.saveAll(fallbackSessions);
    }
}
