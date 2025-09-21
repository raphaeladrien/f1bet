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

        final Provider primaryProvider = factory.getProvider(providerProperties.getPrimary());
        final List<Session> primarySessions = primaryProvider.getSessions(sessionType, validatedYear, country);

        if (!primarySessions.isEmpty() && allParamsProvided(sessionType, year, country)) {
            return primarySessions;
        }

        final Provider fallbackProvider = factory.getProvider(providerProperties.getFallback());
        final List<Session> fallbackSessions = fallbackProvider.getSessions(sessionType, validatedYear, country);
        final List<Session> newSessions = fallbackSessions.stream()
                .filter(session -> !sessionRepository.existsBySessionKey(session.getSessionKey()))
                .peek(session -> {
                    List<Driver> drivers = fallbackProvider.getDrivers(session.getSessionKey()).stream()
                            .peek(driver -> driver.setSession(session))
                            .toList();
                    session.setDrivers(drivers);
                })
                .toList();

        if (!newSessions.isEmpty()) sessionRepository.saveAll(newSessions);

        return primaryProvider.getSessions(sessionType, validatedYear, country);
    }

    private boolean allParamsProvided(final String sessionType, final Integer year, final String country) {
        return sessionType != null && (year != null && year > 0) && country != null;
    }
}
