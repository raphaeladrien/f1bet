package com.sporty.f1bet.interactors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.benmanes.caffeine.cache.Cache;
import com.sporty.f1bet.dto.BetOptionsResponse;
import com.sporty.f1bet.dto.SessionResponse;
import com.sporty.f1bet.entity.Session;
import com.sporty.f1bet.helper.BuilderHelper;
import com.sporty.f1bet.repository.SessionRepository;
import com.sporty.f1bet.service.ProviderOrchestrator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class RetrieveProcessBetOptionsTest {

    private final ProviderOrchestrator orchestrator = mock(ProviderOrchestrator.class);
    private final Cache<String, SessionResponse> eventCache = mock(Cache.class);
    private final SessionRepository sessionRepository = mock(SessionRepository.class);
    private final RetrieveBetOptions retrieveBetOptions =
            new RetrieveBetOptions(orchestrator, eventCache, sessionRepository);

    @Test
    @DisplayName("should refresh sessions via orchestrator before querying repository")
    void shouldRefreshSessionsViaOrchestrator() {
        when(sessionRepository.findBySessionTypeAndYearAndCountry(any(), any(), any(), any(Pageable.class)))
                .thenReturn(Page.empty());

        retrieveBetOptions.execute("RACE", 2024, "BRA", 0, 10);

        verify(orchestrator).getSessions("RACE", 2024, "BRA");
    }

    @Test
    @DisplayName("should return empty BetOptionsResponse when no sessions found")
    void shouldReturnEmptyWhenNoSessionsFound() {
        when(sessionRepository.findBySessionTypeAndYearAndCountry(any(), any(), any(), any(Pageable.class)))
                .thenReturn(Page.empty());

        final BetOptionsResponse response = retrieveBetOptions.execute("RACE", 2024, "BRA", 0, 10);

        assertNotNull(response);
        assertTrue(response.getSessionResponse().isEmpty());
        verify(eventCache, never()).put(any(), any());
    }

    @Test
    @DisplayName("should map sessions to SessionResponse and cache them")
    void shouldMapSessionsAndCacheThem() {
        final Integer sessionKey = 1234;
        final Session session = BuilderHelper.buildSessionWithKey(sessionKey);
        session.setDrivers(List.of(BuilderHelper.buildDriverWithName(session, "Lewis Hamilton")));

        final Page<Session> page = new PageImpl<>(List.of(session));
        when(sessionRepository.findBySessionTypeAndYearAndCountry(
                        eq(Session.SessionType.RACE),
                        eq(session.getYear()),
                        eq(session.getCountryName()),
                        any(Pageable.class)))
                .thenReturn(page);

        final BetOptionsResponse response =
                retrieveBetOptions.execute("RACE", session.getYear(), session.getCountryName(), 0, 10);

        assertEquals(1, response.getSessionResponse().size());
        final SessionResponse sessionResponse = response.getSessionResponse().getFirst();

        assertEquals(session.getCircuit(), sessionResponse.getCircuit());
        assertEquals(session.getCountryName(), sessionResponse.getCountry());
        assertEquals(session.getSessionKey(), sessionResponse.getSessionKey());
        assertEquals(session.getYear(), sessionResponse.getYear());

        assertEquals(1, sessionResponse.getDrivers().size());
        assertEquals("Lewis Hamilton", sessionResponse.getDrivers().getFirst().getName());
        assertEquals(44, sessionResponse.getDrivers().getFirst().getNumber());

        verify(eventCache).put(anyString(), any(SessionResponse.class));
    }

    @Test
    @DisplayName("should support paging correctly")
    void shouldSupportPaging() {
        final Session s1 = BuilderHelper.buildSessionWithKey(1234);
        final Session s2 = BuilderHelper.buildSessionWithKey(5467);

        final Page<Session> page = new PageImpl<>(List.of(s1, s2), PageRequest.of(1, 2), 4);
        when(sessionRepository.findBySessionTypeAndYearAndCountry(
                        eq(Session.SessionType.RACE), eq(s1.getYear()), eq(s1.getCountryName()), any(Pageable.class)))
                .thenReturn(page);

        final BetOptionsResponse response = retrieveBetOptions.execute("RACE", s1.getYear(), s1.getCountryName(), 1, 2);

        assertEquals(2, response.getSessionResponse().size());
        assertEquals(4, response.getTotalElements());
    }
}
