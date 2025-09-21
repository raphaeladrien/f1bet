package com.sporty.f1bet.repository;

import com.sporty.f1bet.entity.Session;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT s FROM Session s LEFT JOIN FETCH s.drivers WHERE (:type IS NULL or s.sessionType = :type) and "
            + "(:year IS NULL or s.year = :year) and "
            + "(:country IS NULL or s.country = :country)")
    Optional<List<Session>> findBySessionTypeAndYearAndCountry(Session.SessionType type, Integer year, String country);

    boolean existsBySessionKey(Integer sessionKey);

    @Query(
            value = "SELECT DISTINCT s FROM Session s LEFT JOIN FETCH s.drivers "
                    + "WHERE (:type IS NULL OR s.sessionType = :type) AND "
                    + "(:year IS NULL OR s.year = :year) AND "
                    + "(:country IS NULL OR s.country = :country)",
            countQuery = "SELECT COUNT(s) FROM Session s " + "WHERE (:type IS NULL OR s.sessionType = :type) AND "
                    + "(:year IS NULL OR s.year = :year) AND "
                    + "(:country IS NULL OR s.country = :country)")
    Page<Session> findBySessionTypeAndYearAndCountry(
            Session.SessionType type, Integer year, String country, Pageable pageable);
}
