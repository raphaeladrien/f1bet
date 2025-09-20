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
        final Provider provider = factory.getProvider(providerProperties.getPrimary());
        final Integer validatedYear = year <= 0 ? null : year;
        final List<Session> sessions = provider.getSessions(sessionType, validatedYear, country);

        if (!sessions.isEmpty()) return sessions;

        final Provider fallbackProvider = factory.getProvider(providerProperties.getFallback());
        final List<Session> fallbackSessions = fallbackProvider.getSessions(sessionType, validatedYear, country);

        for (Session session : fallbackSessions) {
            final List<Driver> drivers = fallbackProvider.getDrivers(session.getSessionKey());
            session.setDrivers(drivers);
        }

        return sessionRepository.saveAll(fallbackSessions);
    }
}
