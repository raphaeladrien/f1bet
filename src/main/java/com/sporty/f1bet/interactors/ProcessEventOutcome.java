package com.sporty.f1bet.interactors;

import com.sporty.f1bet.entity.Bet;
import com.sporty.f1bet.entity.EventOutcome;
import com.sporty.f1bet.entity.User;
import com.sporty.f1bet.repository.BetRepository;
import com.sporty.f1bet.repository.EventOutcomeRepository;
import com.sporty.f1bet.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProcessEventOutcome {

    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final EventOutcomeRepository eventOutcomeRepository;

    public ProcessEventOutcome(
            BetRepository betRepository, UserRepository userRepository, EventOutcomeRepository eventOutcomeRepository) {
        this.betRepository = betRepository;
        this.userRepository = userRepository;
        this.eventOutcomeRepository = eventOutcomeRepository;
    }

    @Transactional
    public Integer processOutcome() {
        final EventOutcome event = eventOutcomeRepository
                .findByStatus(EventOutcome.EventStatus.PENDING)
                .orElse(null);

        if (event == null) {
            return 0;
        }

        final List<Bet> bets = betRepository
                .findBySessionKeyAndStatus(event.getSessionKey(), Bet.BetStatus.PENDING)
                .orElseGet(List::of);

        if (bets.isEmpty()) {
            return 0;
        }

        final List<User> usersToUpdate = bets.stream()
                .peek(bet -> {
                    boolean won = bet.getDriverNumber().equals(event.getWinningDriverNumber());
                    bet.setStatus(won ? Bet.BetStatus.WON : Bet.BetStatus.LOST);

                    if (won) {
                        User user = bet.getUser();
                        user.setBalance(user.getBalance().add(calculatePrize(bet)));
                    }
                })
                .filter(bet -> bet.getStatus() == Bet.BetStatus.WON) // only winners
                .map(Bet::getUser)
                .distinct()
                .toList();

        if (!usersToUpdate.isEmpty()) userRepository.saveAll(usersToUpdate);

        betRepository.saveAll(bets);
        event.setStatus(EventOutcome.EventStatus.FINISHED);
        eventOutcomeRepository.save(event);

        return bets.size();
    }

    private BigDecimal calculatePrize(Bet bet) {
        return bet.getBalance().multiply(BigDecimal.valueOf(bet.getOdd()));
    }
}
