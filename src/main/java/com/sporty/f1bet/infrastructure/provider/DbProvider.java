package com.sporty.f1bet.infrastructure.provider;

import com.sporty.f1bet.application.entity.Driver;
import com.sporty.f1bet.application.entity.Session;
import com.sporty.f1bet.application.provider.Provider;
import com.sporty.f1bet.infrastructure.persistence.repository.DriverRepository;
import com.sporty.f1bet.infrastructure.persistence.repository.SessionRepository;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DbProvider implements Provider {

    private final SessionRepository sessionRepository;
    private final DriverRepository driverRepository;

    public DbProvider(final SessionRepository sessionRepository, final DriverRepository driverRepository) {
        this.sessionRepository = sessionRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public List<Session> getSessions(final String sessionType, final Integer year, final String country) {
        if (year <= 0) {
            throw new InvalidParameterException("Year could not be <= 0");
        }

        final Session.SessionType type = Session.SessionType.fromString(sessionType);
        final Optional<List<Session>> sessions =
                sessionRepository.findBySessionTypeAndYearAndCountry(type, year, country);

        return sessions.orElse(Collections.emptyList());
    }

    @Override
    public List<Driver> getDrivers(Integer sessionKey) {
        final Optional<List<Driver>> drivers = driverRepository.findBySessionId(sessionKey);
        return drivers.orElse(Collections.emptyList());
    }
}
