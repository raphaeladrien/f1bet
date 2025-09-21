package com.sporty.f1bet.interactors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.github.benmanes.caffeine.cache.Cache;
import com.sporty.f1bet.dto.BetOptionsResponse;
import com.sporty.f1bet.dto.SessionResponse;
import com.sporty.f1bet.entity.Driver;
import com.sporty.f1bet.entity.Session;
import com.sporty.f1bet.service.ProviderOrchestrator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RetrieveProcessBetOptionsTest {

    private final ProviderOrchestrator orchestrator = mock(ProviderOrchestrator.class);
    private final Cache<String, SessionResponse> eventCache = mock(Cache.class);
    private final RetrieveBetOptions retrieveBetOptions = new RetrieveBetOptions(orchestrator, eventCache);

    @Test
    @DisplayName("returns BetOptionsResponse and caches each session by ID")
    void shouldReturnBetOptionsResponseWithCachedSessions() {

        Driver driver1 = new Driver("Max VERSTAPPEN", 1, null);
        Driver driver2 = new Driver("Lando NORRIS", 4, null);

        Session.SessionType sessionType = Session.SessionType.RACE;
        Session session = new Session(
                1234, "Sprint", 2025, "Brazil", "BRA", "Race", sessionType, "Interlagos", List.of(driver1, driver2));

        when(orchestrator.getSessions("Race", 2023, "Brazil")).thenReturn(List.of(session));

        BetOptionsResponse result = retrieveBetOptions.execute("Race", 2023, "Brazil");

        assertNotNull(result);
        assertEquals(1, result.getSessionResponse().size());

        SessionResponse response = result.getSessionResponse().get(0);
        assertEquals("Race", response.getType());
        assertEquals("Race", response.getName());
        assertEquals("BRA", response.getCountry());
        assertEquals("Interlagos", response.getCircuit());
        assertEquals(2, response.getDrivers().size());

        verify(eventCache).put(response.getId().toString(), response);
        verify(orchestrator).getSessions("Race", 2023, "Brazil");
    }
}
