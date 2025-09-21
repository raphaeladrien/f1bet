package com.sporty.f1bet.repository;

import com.sporty.f1bet.entity.Bet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    @Query("SELECT b FROM Bet b JOIN FETCH b.user WHERE b.sessionKey = :sessionKey and b.status = :status")
    Optional<List<Bet>> findBySessionKeyAndStatus(Integer sessionKey, Bet.BetStatus status);
}
