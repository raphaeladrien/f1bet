package com.sporty.f1bet.interactors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.benmanes.caffeine.cache.Cache;
import com.sporty.f1bet.dto.DriverResponse;
import com.sporty.f1bet.dto.GenericResponse;
import com.sporty.f1bet.dto.SessionResponse;
import com.sporty.f1bet.entity.Bet;
import com.sporty.f1bet.entity.IdempotencyKey;
import com.sporty.f1bet.entity.User;
import com.sporty.f1bet.repository.BetRepository;
import com.sporty.f1bet.repository.IdempotencyKeyRepository;
import com.sporty.f1bet.repository.UserRepository;
import java.math.BigDecimal;
import java.util.*;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProcessBetTest {

    private final IdempotencyKeyRepository idempotencyKeyRepository = mock(IdempotencyKeyRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final BetRepository betRepository = mock(BetRepository.class);
    private final Cache<String, SessionResponse> eventCache = mock(Cache.class);

    private final ProcessBet processBet =
            new ProcessBet(idempotencyKeyRepository, userRepository, betRepository, eventCache);

    @Test
    @DisplayName("should return existing bet response when idempotency key already exists")
    void shouldReturnExistingBetWhenIdempotencyKeyExists() {
        final UUID idempotencyKey = UUID.randomUUID();
        final UUID betId = UUID.randomUUID();
        when(idempotencyKeyRepository.findById(idempotencyKey))
                .thenReturn(Optional.of(new IdempotencyKey(idempotencyKey, 1L, betId)));

        final GenericResponse response = processBet.execute(1L, BigDecimal.TEN, idempotencyKey, UUID.randomUUID(), 7);

        assertEquals(betId, response.requestId());
        verifyNoInteractions(userRepository, betRepository, eventCache);
    }

    @Test
    @DisplayName("should throw oddsExpiredException when event not found in cache")
    void shouldThrowOddsExpiredWhenEventNotInCache() {
        final UUID eventId = UUID.randomUUID();

        when(idempotencyKeyRepository.findById(any())).thenReturn(Optional.empty());
        when(eventCache.getIfPresent(eventId.toString())).thenReturn(null);

        assertThrows(
                ProcessBet.OddsExpiredException.class,
                () -> processBet.execute(1L, BigDecimal.TEN, UUID.randomUUID(), eventId, 7));

        verifyNoInteractions(userRepository, betRepository);
    }

    @Test
    @DisplayName("should throw UserNotFoundException when user does not exist")
    void shouldThrowUserNotFoundWhenUserDoesNotExist() {
        final UUID eventId = UUID.randomUUID();
        when(idempotencyKeyRepository.findById(any())).thenReturn(Optional.empty());
        when(eventCache.getIfPresent(eventId.toString())).thenReturn(buildSessionResponse(7));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ProcessBet.UserNotFoundException.class,
                () -> processBet.execute(1L, BigDecimal.TEN, UUID.randomUUID(), eventId, 7));
    }

    @Test
    @DisplayName("should throw InsufficientFundsException when user balance is too low")
    void shouldThrowInsufficientFundsWhenBalanceTooLow() {
        final UUID eventId = UUID.randomUUID();
        when(idempotencyKeyRepository.findById(any())).thenReturn(Optional.empty());
        when(eventCache.getIfPresent(eventId.toString())).thenReturn(buildSessionResponse(7));

        final User user = buildUser(1L, BigDecimal.ONE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(
                ProcessBet.InsufficientFundsException.class,
                () -> processBet.execute(1L, BigDecimal.TEN, UUID.randomUUID(), eventId, 7));
    }

    @Test
    @DisplayName("should throw OddsNotFoundException when driver not found in session response")
    void shouldThrowOddsNotFoundWhenDriverNotInSession() {
        final UUID eventId = UUID.randomUUID();
        when(idempotencyKeyRepository.findById(any())).thenReturn(Optional.empty());
        when(eventCache.getIfPresent(eventId.toString())).thenReturn(buildSessionResponse(99));

        final User user = buildUser(1L, BigDecimal.valueOf(100));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(
                ProcessBet.OddsNotFoundException.class,
                () -> processBet.execute(1L, BigDecimal.TEN, UUID.randomUUID(), eventId, 7));
    }

    @Test
    @DisplayName("should process bet successfully with valid inputs and update balance")
    void shouldProcessBetSuccessfully() {
        final UUID eventId = UUID.randomUUID();
        final UUID idempotencyKey = UUID.randomUUID();
        final UUID betId = UUID.randomUUID();

        when(idempotencyKeyRepository.findById(idempotencyKey)).thenReturn(Optional.empty());
        final SessionResponse sessionResponse = buildSessionResponse(7);
        when(eventCache.getIfPresent(eventId.toString())).thenReturn(sessionResponse);

        User user = buildUser(1L, BigDecimal.valueOf(100));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        final Bet savedBet =
                new Bet(BigDecimal.TEN, Bet.BetStatus.PENDING, user, sessionResponse.getSessionKey(), 7, 3);
        savedBet.setId(betId);

        when(betRepository.save(any(Bet.class))).thenReturn(savedBet);

        final GenericResponse response = processBet.execute(1L, BigDecimal.TEN, idempotencyKey, eventId, 7);

        assertEquals(betId, response.requestId());
        assertEquals(BigDecimal.valueOf(90), user.getBalance()); // balance reduced

        verify(userRepository).save(user);
        verify(betRepository).save(any(Bet.class));
        verify(idempotencyKeyRepository).save(any(IdempotencyKey.class));
    }

    private User buildUser(final Long id, final BigDecimal balance) {
        final User user = new User();
        user.setId(id);
        user.setBalance(balance);
        return user;
    }

    private SessionResponse buildSessionResponse(final Integer driverNumber) {
        final DriverResponse driver = new DriverResponse("a-name", driverNumber);
        final SessionResponse session = new SessionResponse("a-type", "a-name", "BLA", "a-race-track", 1234, 2025);
        session.addDriver(driver);
        return session;
    }
}
