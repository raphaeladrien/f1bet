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
    @DisplayName("should return primary sessions when not empty and all params provided")
    void shouldReturnPrimarySessionsWhenNotEmptyAndAllParamsProvided() {
        final Session session = BuilderHelper.buildSessionWithKey(1234);
        when(primaryProvider.getSessions("RACE", 2024, "Italy")).thenReturn(List.of(session));

        final List<Session> result = orchestrator.getSessions("RACE", 2024, "Italy");

        assertEquals(1, result.size());
        assertEquals(1234, result.get(0).getSessionKey());

        verifyNoInteractions(fallbackProvider, sessionRepository);
    }

    @Test
    @DisplayName("should fallback when primary empty even if all params provided")
    void shouldFallbackWhenPrimaryEmptyEvenIfAllParamsProvided() {
        final Integer sessionKey = 1234;
        final Session session = BuilderHelper.buildSessionWithKey(sessionKey);

        when(primaryProvider.getSessions("RACE", 2024, "Italy")).thenReturn(Collections.emptyList());
        when(fallbackProvider.getSessions("RACE", 2024, "Italy")).thenReturn(List.of(session));
        when(sessionRepository.existsBySessionKey(sessionKey)).thenReturn(false);
        when(fallbackProvider.getDrivers(sessionKey))
                .thenReturn(List.of(BuilderHelper.buildDriverWithName(null, "Max")));

        final List<Session> result = orchestrator.getSessions("RACE", 2024, "Italy");

        assertTrue(result.isEmpty());

        verify(sessionRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("should fallback when primary not empty but missing some params")
    void shouldFallbackWhenPrimaryNotEmptyButMissingSomeParams() {
        final Integer primarySessionKey = 1234;
        final Integer fallbackSessionKey = 5678;
        Session primarySession = BuilderHelper.buildSessionWithKey(primarySessionKey);
        Session fallbackSession = BuilderHelper.buildSessionWithKey(fallbackSessionKey);

        when(primaryProvider.getSessions("RACE", null, null)).thenReturn(List.of(primarySession));
        when(fallbackProvider.getSessions("RACE", null, null)).thenReturn(List.of(fallbackSession));
        when(sessionRepository.existsBySessionKey(fallbackSessionKey)).thenReturn(false);
        when(fallbackProvider.getDrivers(fallbackSessionKey))
                .thenReturn(List.of(BuilderHelper.buildDriverWithName(fallbackSession, "Max")));

        final List<Session> result = orchestrator.getSessions("RACE", null, null);

        assertEquals(List.of(primarySession), result);

        verify(sessionRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("should not save sessions if all fallback sessions already exist")
    void shouldNotSaveIfAllFallbackSessionsAlreadyExist() {
        final Integer sessionKey = 1234;
        final Session fallbackSession = BuilderHelper.buildSessionWithKey(sessionKey);

        when(primaryProvider.getSessions("RACE", 2024, "UK")).thenReturn(Collections.emptyList());
        when(fallbackProvider.getSessions("RACE", 2024, "UK")).thenReturn(List.of(fallbackSession));
        when(sessionRepository.existsBySessionKey(sessionKey)).thenReturn(true);

        final List<Session> result = orchestrator.getSessions("RACE", 2024, "UK");

        assertTrue(result.isEmpty());
        verify(sessionRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("should attach drivers from fallback provider before saving sessions")
    void shouldAttachDriversFromFallbackProviderBeforeSaving() {
        final Integer sessionKey = 1234;
        final Session fallbackSession = BuilderHelper.buildSessionWithKey(sessionKey);
        final Driver driver = BuilderHelper.buildDriverWithName(fallbackSession, "Charles");

        when(primaryProvider.getSessions("RACE", 2024, "Monaco")).thenReturn(Collections.emptyList());
        when(fallbackProvider.getSessions("RACE", 2024, "Monaco")).thenReturn(List.of(fallbackSession));
        when(sessionRepository.existsBySessionKey(sessionKey)).thenReturn(false);
        when(fallbackProvider.getDrivers(sessionKey)).thenReturn(List.of(driver));

        orchestrator.getSessions("RACE", 2024, "Monaco");

        assertNotNull(fallbackSession.getDrivers());
        assertEquals("Charles", fallbackSession.getDrivers().getFirst().getFullName());
        assertEquals(fallbackSession, fallbackSession.getDrivers().getFirst().getSession());

        verify(sessionRepository).saveAll(List.of(fallbackSession));
    }

    @Test
    @DisplayName("should treat invalid year (<=0) as null")
    void shouldTreatInvalidYearAsNull() {
        when(primaryProvider.getSessions("RACE", null, "France")).thenReturn(Collections.emptyList());
        when(fallbackProvider.getSessions("RACE", null, "France")).thenReturn(Collections.emptyList());

        final List<Session> result = orchestrator.getSessions("RACE", -1, "France");

        assertTrue(result.isEmpty());
        verify(primaryProvider, times(2)).getSessions("RACE", null, "France");
        verify(fallbackProvider).getSessions("RACE", null, "France");
    }
}
