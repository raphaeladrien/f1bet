package com.sporty.f1bet.provider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sporty.f1bet.entity.Driver;
import com.sporty.f1bet.entity.Session;
import com.sporty.f1bet.helper.BuilderHelper;
import com.sporty.f1bet.repository.DriverRepository;
import com.sporty.f1bet.repository.SessionRepository;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DbProviderTest {

    private final SessionRepository sessionRepository = mock(SessionRepository.class);
    private final DriverRepository driverRepository = mock(DriverRepository.class);

    private final DbProvider subject = new DbProvider(sessionRepository, driverRepository);

    private final String sessionType = "PRACTICE";
    private final String country = "BEL";
    private final Session session = BuilderHelper.buildSession();
    private final Driver driver = BuilderHelper.buildDriver();

    @Test
    @DisplayName("returns matching sessions when key parameters are supplied")
    void shouldReturnSessionWhenDataMatchingWithParamProvided() {
        Session.SessionType type = Session.SessionType.fromString(sessionType);
        List<Session> expectedSessions = List.of(session);

        when(sessionRepository.findBySessionTypeAndYearAndCountry(type, 2023, country))
                .thenReturn(Optional.of(expectedSessions));

        List<Session> result = subject.getSessions(sessionType, 2023, country);

        assertEquals(1, result.size());
        assertEquals(session, result.get(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("throws InvalidParameterException when an invalid year <= 0 is provided")
    void wheninvalidYearProvidedthrowsInvalidParameterException(Integer year) {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class, () -> subject.getSessions(sessionType, year, country));

        assertEquals("Year could not be <= 0", exception.getMessage());
    }

    @Test
    @DisplayName("returns an empty list, when parameters provided doesn't match")
    void returnsEmptyListWhenParametersProvidedDoesNotMatch() {
        Session.SessionType type = Session.SessionType.fromString(sessionType);

        when(sessionRepository.findBySessionTypeAndYearAndCountry(type, 2022, country))
                .thenReturn(Optional.empty());

        List<Session> result = subject.getSessions(sessionType, 2022, country);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("returns the drivers associated with the provided session key")
    void returnsDriversAssociatedProvidedSessionKey() {
        List<Driver> expectedDrivers = List.of(driver);

        when(driverRepository.findBySessionId(101)).thenReturn(Optional.of(expectedDrivers));

        List<Driver> result = subject.getDrivers(101);

        assertEquals(1, result.size());
        assertEquals(driver, result.getFirst());
    }

    @Test
    @DisplayName("when no drivers associated with the provided session key, returns an empty list")
    void whenNoDriversAssociatedProvidedSessionKeyReturnsEmpty() {
        when(driverRepository.findBySessionId(999)).thenReturn(Optional.empty());

        List<Driver> result = subject.getDrivers(999);

        assertTrue(result.isEmpty());
    }
}
