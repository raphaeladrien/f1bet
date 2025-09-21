package com.sporty.f1bet.interactors;

import com.github.benmanes.caffeine.cache.Cache;
import com.sporty.f1bet.dto.BetOptionsResponse;
import com.sporty.f1bet.dto.DriverResponse;
import com.sporty.f1bet.dto.SessionResponse;
import com.sporty.f1bet.entity.Session;
import com.sporty.f1bet.service.ProviderOrchestrator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RetrieveBetOptions {

    private final ProviderOrchestrator orchestrator;
    private final Cache<String, SessionResponse> eventCache;

    public RetrieveBetOptions(
            final ProviderOrchestrator orchestrator, final Cache<String, SessionResponse> eventCache) {
        this.orchestrator = orchestrator;
        this.eventCache = eventCache;
    }

    public BetOptionsResponse execute(final String sessionType, final Integer year, final String country) {
        final List<Session> sessions = orchestrator.getSessions(sessionType, year, country);
        final List<SessionResponse> response = sessions.stream()
                .map(session -> {
                    SessionResponse sessionResponse = new SessionResponse(
                            session.getSessionType().getLabel(),
                            session.getSessionName(),
                            session.getCountryName(),
                            session.getCircuit(),
                            session.getSessionKey());

                    session.getDrivers().stream()
                            .map(driver -> new DriverResponse(driver.getFullName(), driver.getDriverNumber()))
                            .forEach(sessionResponse::addDriver);

                    eventCache.put(sessionResponse.getId().toString(), sessionResponse);
                    return sessionResponse;
                })
                .collect(Collectors.toList());

        return new BetOptionsResponse(response);
    }
}
