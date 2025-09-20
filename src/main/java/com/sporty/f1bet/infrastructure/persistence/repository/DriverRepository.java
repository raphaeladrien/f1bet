package com.sporty.f1bet.infrastructure.persistence.repository;

import com.sporty.f1bet.application.entity.Driver;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d FROM Driver d WHERE d.session.sessionKey = :sessionKey")
    Optional<List<Driver>> findBySessionId(Integer sessionKey);
}
