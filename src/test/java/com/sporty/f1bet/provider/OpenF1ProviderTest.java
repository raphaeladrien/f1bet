package com.sporty.f1bet.provider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.sporty.f1bet.entity.Driver;
import com.sporty.f1bet.entity.Session;
import com.sporty.f1bet.mapper.DriverMapper;
import com.sporty.f1bet.mapper.SessionMapper;
import com.sporty.f1bet.provider.dto.DriverDTO;
import com.sporty.f1bet.provider.dto.SessionDTO;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
public class OpenF1ProviderTest {

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private OpenF1Provider subject;

    @ParameterizedTest
    @MethodSource("sessionParams")
    @DisplayName("returns sessions immediately without triggering retry mechanism")
    void returnSessionsWithoutTriggeringRetry(final String sessionType, final Integer year, final String country) {
        final SessionDTO sessionDTO = buildSessionDTO();
        final Session expectedSession = SessionMapper.toEntity(sessionDTO);
        final List<SessionDTO> mockSessions = List.of(sessionDTO, buildSessionDTO());

        final String uri = buildSessionUri(sessionType, year, country);

        when(restTemplate.exchange(
                        eq(uri),
                        eq(org.springframework.http.HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(mockSessions));

        final List<Session> result = subject.getSessions(sessionType, year, country);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedSession, result.getFirst());

        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
    }

    @ParameterizedTest
    @MethodSource("sessionParams")
    @DisplayName("returns empty sessions list when the response payload is null")
    void returnEmptySessionListWhenResponsePayloadIsNull(
            final String sessionType, final Integer year, final String country) {
        final String uri = buildSessionUri(sessionType, year, country);

        when(restTemplate.exchange(
                        eq(uri),
                        eq(org.springframework.http.HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(null));

        final List<Session> result = subject.getSessions(sessionType, year, country);

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("sessionParams")
    @DisplayName("returns sessions successfully after retrying failed initial attempt")
    void returnSessionsAfterRetryingFailed(final String sessionType, final Integer year, final String country) {
        final SessionDTO sessionDTO = buildSessionDTO();
        final Session expectedSession = SessionMapper.toEntity(sessionDTO);
        final List<SessionDTO> mockSessions = List.of(sessionDTO);
        final String uri = buildSessionUri(sessionType, year, country);

        when(restTemplate.exchange(
                        eq(uri),
                        eq(org.springframework.http.HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class)))
                .thenThrow(new RestClientException("First failure"))
                .thenThrow(new RestClientException("Second failure"))
                .thenReturn(ResponseEntity.ok(mockSessions));

        final List<Session> result = subject.getSessions(sessionType, year, country);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedSession, result.getFirst());

        verify(restTemplate, times(3)).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
    }

    private static String buildSessionUri(String sessionType, Integer year, String country) {
        final String uri = UriComponentsBuilder.fromUriString("https://api.openf1.org/v1/sessions")
                .queryParamIfPresent("session_type", Optional.ofNullable(sessionType))
                .queryParamIfPresent("year", Optional.ofNullable(year))
                .queryParamIfPresent("country_code", Optional.ofNullable(country))
                .build()
                .encode()
                .toUri()
                .toString();
        return uri;
    }

    static Stream<Arguments> sessionParams() {
        return Stream.of(
                Arguments.of("Race", 2023, "BEL"),
                Arguments.of("Race", 2023, null),
                Arguments.of("Race", null, null),
                Arguments.of(null, 2023, "BEL"),
                Arguments.of(null, 2023, null),
                Arguments.of(null, null, "BEL"),
                Arguments.of(null, null, null));
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
