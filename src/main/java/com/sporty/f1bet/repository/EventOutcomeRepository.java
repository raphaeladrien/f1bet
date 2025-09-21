package com.sporty.f1bet.repository;

import com.sporty.f1bet.entity.EventOutcome;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventOutcomeRepository extends JpaRepository<EventOutcome, Long> {
    Optional<EventOutcome> findByStatus(EventOutcome.EventStatus status);
}
