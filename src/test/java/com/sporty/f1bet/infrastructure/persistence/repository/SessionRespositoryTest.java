package com.sporty.f1bet.infrastructure.persistence.repository;

import com.sporty.f1bet.infrastructure.persistence.entity.Driver;
import com.sporty.f1bet.infrastructure.persistence.entity.Session;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class SessionRespositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void setup() {
        final Session session = new Session(
                7,
                "Spa-Francorchamps",
                2023,
                "BEL",
                "Belgium",
                "Belgium GP",
                Session.SessionType.PRACTICE,
                Collections.emptyList());

        final Driver driver = new Driver("Ayrton Senna", 12, session);
        session.addDriver(driver);
        sessionRepository.save(session);
    }

    @ParameterizedTest
    @MethodSource("sessionParams")
    @DisplayName("returns matching sessions when one or more filter parameters are supplied\n")
    void returnSessionForParameterProvided(String type, Integer year, String country) {
        final Session.SessionType sessionType = Session.SessionType.fromString(type);
        final Optional<List<Session>> optionalResult =
                sessionRepository.findBySessionTypeAndYearAndCountry(sessionType, year, country);

        Assertions.assertFalse(optionalResult.isEmpty());
        final List<Session> result = optionalResult.get();
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertFalse(result.getFirst().getDrivers().isEmpty());
    }

    static Stream<Arguments> sessionParams() {
        return Stream.of(
                Arguments.of("PRACTICE", 2023, "BEL"),
                Arguments.of("PRACTICE", 2023, null),
                Arguments.of("PRACTICE", null, null),
                Arguments.of(null, 2023, "BEL"),
                Arguments.of(null, 2023, null),
                Arguments.of(null, null, "BEL"),
                Arguments.of(null, null, null));
    }
}
