package com.sporty.f1bet.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sporty.f1bet.entity.Driver;
import com.sporty.f1bet.entity.Session;
import java.util.List;
import java.util.Optional;

import com.sporty.f1bet.repository.DriverRepository;
import com.sporty.f1bet.repository.SessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    @DisplayName("returns matching drivers when session key parameter is supplied")
    void shouldReturnMatchingDriverWhenSessionKeyParam() {
        Session session = new Session(1234, "a-name", 2023, "BEL", "Belgium", "Spa", Session.SessionType.RACE, null);
        session = sessionRepository.save(session);

        Driver driver1 = new Driver("Lewis Hamilton", 44, session);
        Driver driver2 = new Driver("Max Verstappen", 1, session);
        driverRepository.saveAll(List.of(driver1, driver2));

        Optional<List<Driver>> result = driverRepository.findBySessionId(session.getSessionKey());

        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(2);
        assertThat(result.get())
                .extracting(Driver::getFullName)
                .containsExactlyInAnyOrder("Lewis Hamilton", "Max Verstappen");
    }

    @Test
    @DisplayName("returns an empty list when no drivers are associated with the provided session key")
    void shouldReturnEmptyWhenNoDriversMatchSessionId() {
        Optional<List<Driver>> result = driverRepository.findBySessionId(999);

        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }
}
