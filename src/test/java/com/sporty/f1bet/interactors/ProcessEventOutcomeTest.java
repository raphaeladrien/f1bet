package com.sporty.f1bet.interactors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sporty.f1bet.entity.Bet;
import com.sporty.f1bet.entity.EventOutcome;
import com.sporty.f1bet.entity.User;
import com.sporty.f1bet.repository.BetRepository;
import com.sporty.f1bet.repository.EventOutcomeRepository;
import com.sporty.f1bet.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProcessOutcomeServiceTest {

    private final EventOutcomeRepository eventOutcomeRepository = mock(EventOutcomeRepository.class);
    private final BetRepository betRepository = mock(BetRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    private final ProcessEventOutcome processEventOutcome =
            new ProcessEventOutcome(betRepository, userRepository, eventOutcomeRepository);

    @Test
    @DisplayName("should return 0 when there is no pending event")
    void shouldReturnZeroWhenNoPendingEvent() {
        when(eventOutcomeRepository.findByStatus(EventOutcome.EventStatus.PENDING))
                .thenReturn(Optional.empty());

        final int result = processEventOutcome.processOutcome();

        assertEquals(0, result);
        verifyNoInteractions(betRepository, userRepository);
    }

    @Test
    @DisplayName("should return 0 when event found but no pending bets")
    void shouldReturnZeroWhenNoPendingBets() {
        final EventOutcome event = new EventOutcome();
        event.setSessionKey(12345);

        when(eventOutcomeRepository.findByStatus(EventOutcome.EventStatus.PENDING))
                .thenReturn(Optional.of(event));
        when(betRepository.findBySessionKeyAndStatus(12345, Bet.BetStatus.PENDING))
                .thenReturn(Optional.empty());

        final int result = processEventOutcome.processOutcome();

        assertEquals(0, result);
        verify(eventOutcomeRepository, never()).save(event);
    }

    @Test
    @DisplayName("should mark bets as LOST when no winners exist")
    void shouldMarkBetsAsLostWhenNoWinners() {
        final EventOutcome event = new EventOutcome();
        event.setSessionKey(12345);
        event.setWinningDriverNumber(44);

        final Bet bet = new Bet();
        bet.setDriverNumber(1);
        bet.setStatus(Bet.BetStatus.PENDING);
        bet.setBalance(BigDecimal.TEN);

        when(eventOutcomeRepository.findByStatus(EventOutcome.EventStatus.PENDING))
                .thenReturn(Optional.of(event));
        when(betRepository.findBySessionKeyAndStatus(12345, Bet.BetStatus.PENDING))
                .thenReturn(Optional.of(List.of(bet)));

        final int result = processEventOutcome.processOutcome();

        assertEquals(1, result);
        assertEquals(Bet.BetStatus.LOST, bet.getStatus());
        verify(userRepository, never()).saveAll(any());
        verify(betRepository).saveAll(List.of(bet));
        assertEquals(EventOutcome.EventStatus.FINISHED, event.getStatus());
    }

    @Test
    @DisplayName("should mark bet as WON and update user balance for single winning bet")
    void shouldMarkBetsAsWonAndUpdateUserBalance() {

        final EventOutcome event = new EventOutcome();
        event.setSessionKey(12345);
        event.setWinningDriverNumber(44);

        final User user = new User();
        user.setBalance(BigDecimal.valueOf(100));

        final Bet bet = new Bet();
        bet.setDriverNumber(44);
        bet.setStatus(Bet.BetStatus.PENDING);
        bet.setBalance(BigDecimal.TEN);
        bet.setOdd(2);
        bet.setUser(user);

        when(eventOutcomeRepository.findByStatus(EventOutcome.EventStatus.PENDING))
                .thenReturn(Optional.of(event));
        when(betRepository.findBySessionKeyAndStatus(12345, Bet.BetStatus.PENDING))
                .thenReturn(Optional.of(List.of(bet)));

        final int result = processEventOutcome.processOutcome();

        assertEquals(1, result);
        assertEquals(Bet.BetStatus.WON, bet.getStatus());

        assertEquals(BigDecimal.valueOf(120), user.getBalance());

        verify(userRepository).saveAll(List.of(user));
        verify(betRepository).saveAll(List.of(bet));
        verify(eventOutcomeRepository).save(event);
        assertEquals(EventOutcome.EventStatus.FINISHED, event.getStatus());
    }

    @Test
    @DisplayName("should handle multiple bets and update user balance only once per user")
    void shouldHandleMultipleBetsAndOnlyUpdateWinningUsersOnce() {
        final EventOutcome event = new EventOutcome();
        event.setSessionKey(12345);
        event.setWinningDriverNumber(44);

        final User user = new User();
        user.setBalance(BigDecimal.valueOf(50));

        final Bet winningBet1 = new Bet();
        winningBet1.setDriverNumber(44);
        winningBet1.setStatus(Bet.BetStatus.PENDING);
        winningBet1.setBalance(BigDecimal.valueOf(20));
        winningBet1.setOdd(2);
        winningBet1.setUser(user);

        final Bet winningBet2 = new Bet();
        winningBet2.setDriverNumber(44);
        winningBet2.setStatus(Bet.BetStatus.PENDING);
        winningBet2.setBalance(BigDecimal.valueOf(10));
        winningBet2.setOdd(3);
        winningBet2.setUser(user);

        final Bet losingBet = new Bet();
        losingBet.setDriverNumber(1);
        losingBet.setStatus(Bet.BetStatus.PENDING);
        losingBet.setBalance(BigDecimal.TEN);
        losingBet.setOdd(3);
        losingBet.setUser(user);

        when(eventOutcomeRepository.findByStatus(EventOutcome.EventStatus.PENDING))
                .thenReturn(Optional.of(event));
        when(betRepository.findBySessionKeyAndStatus(12345, Bet.BetStatus.PENDING))
                .thenReturn(Optional.of(List.of(winningBet1, winningBet2, losingBet)));

        final int result = processEventOutcome.processOutcome();

        assertEquals(3, result);

        assertEquals(Bet.BetStatus.WON, winningBet1.getStatus());
        assertEquals(Bet.BetStatus.WON, winningBet2.getStatus());
        assertEquals(Bet.BetStatus.LOST, losingBet.getStatus());

        assertEquals(BigDecimal.valueOf(120), user.getBalance());

        verify(userRepository).saveAll(List.of(user));
        verify(betRepository).saveAll(any());
        verify(eventOutcomeRepository).save(event);
    }
}
