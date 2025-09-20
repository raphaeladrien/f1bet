package com.sporty.f1bet.infrastructure.persistence.repository;

import com.sporty.f1bet.application.entity.Session;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT s FROM Session s LEFT JOIN FETCH s.drivers WHERE (:type IS NULL or s.sessionType = :type) and "
            + "(:year IS NULL or s.year = :year) and "
            + "(:country IS NULL or s.country = :country)")
    Optional<List<Session>> findBySessionTypeAndYearAndCountry(Session.SessionType type, Integer year, String country);
}
