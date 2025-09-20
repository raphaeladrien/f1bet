package com.sporty.f1bet.infrastructure.provider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.sporty.f1bet.infrastructure.mapper.DriverMapper;
import com.sporty.f1bet.infrastructure.mapper.SessionMapper;
import com.sporty.f1bet.infrastructure.persistence.entity.Driver;
import com.sporty.f1bet.infrastructure.persistence.entity.Session;
import com.sporty.f1bet.infrastructure.provider.dto.DriverDTO;
import com.sporty.f1bet.infrastructure.provider.dto.SessionDTO;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class OpenF1ProviderTest {

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private OpenF1Provider subject;

    @Test
    @DisplayName("returns sessions immediately without triggering retry mechanism")
    void returnSessionsWithoutTriggeringRetry() {
        final SessionDTO sessionDTO = buildSessionDTO();
        final Session expectedSession = SessionMapper.toEntity(sessionDTO);
        final List<SessionDTO> mockSessions = List.of(sessionDTO, buildSessionDTO());

        when(restTemplate.exchange(
                        eq("https://api.openf1.org/v1/sessions"),
                        eq(org.springframework.http.HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(mockSessions));

        final List<Session> result = subject.getSessions();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedSession, result.getFirst());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("returns empty sessions list when the response payload is null")
    void returnEmptySessionListWhenResponsePayloadIsNull() {
        when(restTemplate.exchange(
                        eq("https://api.openf1.org/v1/sessions"),
                        eq(org.springframework.http.HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(null));

        final List<Session> result = subject.getSessions();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("returns sessions successfully after retrying failed initial attempt")
    void returnSessionsAfterRetryingFailed() {
        final SessionDTO sessionDTO = buildSessionDTO();
        final Session expectedSession = SessionMapper.toEntity(sessionDTO);
        final List<SessionDTO> mockSessions = List.of(sessionDTO);

        when(restTemplate.exchange(
                        eq("https://api.openf1.org/v1/sessions"),
                        eq(org.springframework.http.HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class)))
                .thenThrow(new RestClientException("First failure"))
                .thenThrow(new RestClientException("Second failure"))
                .thenReturn(ResponseEntity.ok(mockSessions));

        final List<Session> result = subject.getSessions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedSession, result.getFirst());

        verify(restTemplate, times(3)).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("returns drivers immediately without triggering retry mechanism")
    void returnDriversWithoutTriggeringRetry() {
        final Integer sessionKey = 1234;
        final DriverDTO driverDTO = buildDriverDTO();
        final Driver expectedDriver = DriverMapper.toEntity(driverDTO);
        final List<DriverDTO> mockDrivers = List.of(driverDTO, buildDriverDTO());

        when(restTemplate.exchange(
                        eq("https://api.openf1.org/v1/drivers?session_key=" + sessionKey),
                        eq(org.springframework.http.HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(mockDrivers));

        final List<Driver> result = subject.getDrivers(sessionKey);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDriver, result.getFirst());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("returns empty drivers list when the response payload is null")
    void returnEmptyDriverListWhenResponsePayloadIsNull() {
        final Integer sessionKey = 1234;

        when(restTemplate.exchange(
                        eq("https://api.openf1.org/v1/drivers?session_key=" + sessionKey),
                        eq(org.springframework.http.HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(null));

        final List<Driver> result = subject.getDrivers(sessionKey);

        assertTrue(result.isEmpty());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("returns drivers successfully after retrying failed initial attempt")
    void returnDriversAfterRetryingFailed() {
        final Integer sessionKey = 1234;
        final DriverDTO driverDTO = buildDriverDTO();
        final Driver expectedDriver = DriverMapper.toEntity(driverDTO);
        final List<DriverDTO> mockDrivers = List.of(driverDTO, buildDriverDTO());

        when(restTemplate.exchange(
                        eq("https://api.openf1.org/v1/drivers?session_key=" + sessionKey),
                        eq(org.springframework.http.HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class)))
                .thenThrow(new RestClientException("First failure"))
                .thenThrow(new RestClientException("Second failure"))
                .thenReturn(ResponseEntity.ok(mockDrivers));

        final List<Driver> result = subject.getDrivers(sessionKey);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDriver, result.getFirst());

        verify(restTemplate, times(3)).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
    }

    private SessionDTO buildSessionDTO() {
        return new SessionDTO(
                7,
                "Spa-Francorchamps",
                "BEL",
                16,
                "Belgium",
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC),
                "02:00:00",
                "Spa-Francorchamps",
                "Sprint",
                9140,
                "Race",
                2023);
    }

    private DriverDTO buildDriverDTO() {
        return new DriverDTO(
                9158,
                1,
                "M Vesrstappen",
                "Max Verstappen",
                "VER",
                "Red Bull",
                "3671C6",
                "Max",
                "http://a-super-max-url.com",
                "NED");
    }
}
