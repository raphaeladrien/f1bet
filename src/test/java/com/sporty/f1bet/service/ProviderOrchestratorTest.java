package com.sporty.f1bet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sporty.f1bet.config.ProviderProperties;
import com.sporty.f1bet.entity.Driver;
import com.sporty.f1bet.entity.Session;
import com.sporty.f1bet.helper.BuilderHelper;
import com.sporty.f1bet.provider.Provider;
import com.sporty.f1bet.provider.ProviderFactory;
import com.sporty.f1bet.repository.SessionRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProviderOrchestratorTest {

    private final ProviderFactory factory = mock(ProviderFactory.class);

    private final ProviderProperties providerProperties = mock(ProviderProperties.class);
    private final SessionRepository sessionRepository = mock(SessionRepository.class);
    private final Provider primaryProvider = mock(Provider.class);
    private final Provider fallbackProvider = mock(Provider.class);

    private ProviderOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        when(providerProperties.getPrimary()).thenReturn("primary");
        when(providerProperties.getFallback()).thenReturn("fallback");
        when(factory.getProvider("primary")).thenReturn(primaryProvider);
        when(factory.getProvider("fallback")).thenReturn(fallbackProvider);

        orchestrator = new ProviderOrchestrator(factory, providerProperties, sessionRepository);
    }

    @Test
    @DisplayName("returns primary provider sessions when available")
    void shouldReturnPrimarySessionsWhenAvailable() {
        final Session session = BuilderHelper.buildSession();
        final List<Session> sessions = List.of(session);

        when(primaryProvider.getSessions("race", 2023, "UK")).thenReturn(sessions);

        final List<Session> result = orchestrator.getSessions("race", 2023, "UK");

        assertEquals(1, result.size());
        verify(sessionRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("uses fallback provider when primary is empty")
    void shouldFallbackWhenPrimaryReturnsEmpty() {
        final Session fallbackSession = BuilderHelper.buildSession();
        fallbackSession.setSessionKey(1234);

        final List<Session> fallbackSessions = List.of(fallbackSession);
        final List<Driver> drivers = List.of(BuilderHelper.buildDriver(null));

        when(primaryProvider.getSessions("qualifying", 2023, "UK")).thenReturn(Collections.emptyList());
        when(fallbackProvider.getSessions("qualifying", 2023, "UK")).thenReturn(fallbackSessions);
        when(fallbackProvider.getDrivers(1234)).thenReturn(drivers);
        when(sessionRepository.saveAll(fallbackSessions)).thenReturn(fallbackSessions);

        List<Session> result = orchestrator.getSessions("qualifying", 2023, "UK");

        assertEquals(1, result.size());
        assertEquals(drivers, result.getFirst().getDrivers());
        verify(sessionRepository).saveAll(fallbackSessions);
    }
}
