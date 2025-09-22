package com.sporty.f1bet.interactors;

import com.github.benmanes.caffeine.cache.Cache;
import com.sporty.f1bet.dto.BetOptionsResponse;
import com.sporty.f1bet.dto.DriverResponse;
import com.sporty.f1bet.dto.SessionResponse;
import com.sporty.f1bet.entity.Session;
import com.sporty.f1bet.repository.SessionRepository;
import com.sporty.f1bet.service.ProviderOrchestrator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RetrieveBetOptions {

    public static final List<String> ISO_ALPHA3_CODES = Arrays.stream(Locale.getISOCountries())
            .map(code -> Locale.of("", code).getISO3Country())
            .collect(Collectors.toList());

    private final ProviderOrchestrator orchestrator;
    private final Cache<String, SessionResponse> eventCache;
    private final SessionRepository sessionRepository;

    public RetrieveBetOptions(
            final ProviderOrchestrator orchestrator,
            final Cache<String, SessionResponse> eventCache,
            final SessionRepository sessionRepository) {
        this.orchestrator = orchestrator;
        this.eventCache = eventCache;
        this.sessionRepository = sessionRepository;
    }

    public BetOptionsResponse execute(
            final String sessionType,
            final Integer year,
            final String country,
            final Integer page,
            final Integer size) {

        if (country == null || ISO_ALPHA3_CODES.stream().noneMatch(code -> code.equalsIgnoreCase(country))) {
            throw new InvalidCountryCodeException("Invalid or missing ISO 3166-1 alpha-3 country code: " + country);
        }

        refreshSessions(sessionType, year, country);
        final Pageable pageable = PageRequest.of(page, size);
        final Page<Session> sessions = sessionRepository.findBySessionTypeAndYearAndCountry(
                Session.SessionType.fromString(sessionType), year, country, pageable);

        final Page<SessionResponse> pagedSessions = sessions.map(session -> {
            SessionResponse sessionResponse = new SessionResponse(
                    session.getSessionType().getLabel(),
                    session.getSessionName(),
                    session.getCountryName(),
                    session.getCircuit(),
                    session.getSessionKey(),
                    session.getYear());

            session.getDrivers().stream()
                    .map(driver -> new DriverResponse(driver.getFullName(), driver.getDriverNumber()))
                    .forEach(sessionResponse::addDriver);

            eventCache.put(sessionResponse.getId().toString(), sessionResponse);
            return sessionResponse;
        });

        return new BetOptionsResponse(pagedSessions);
    }

    private void refreshSessions(final String sessionType, final Integer year, final String country) {
        orchestrator.getSessions(sessionType, year, country);
    }

    public static class InvalidCountryCodeException extends RuntimeException {
        public InvalidCountryCodeException(String message) {
            super(message);
        }
    }
}
