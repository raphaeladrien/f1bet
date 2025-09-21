package com.sporty.f1bet.interactors;

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
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class ProcessBet {

    private static final Logger logger = LoggerFactory.getLogger(ProcessBet.class);

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final UserRepository userRepository;
    private final BetRepository betRepository;
    private final Cache<String, SessionResponse> eventCache;

    public ProcessBet(
            IdempotencyKeyRepository idempotencyKeyRepository,
            UserRepository userRepository,
            BetRepository betRepository,
            Cache<String, SessionResponse> eventCache) {
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.userRepository = userRepository;
        this.betRepository = betRepository;
        this.eventCache = eventCache;
    }

    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 50, multiplier = 2))
    @Transactional
    public GenericResponse execute(
            Long userId, BigDecimal amount, UUID idempotencyKey, UUID eventId, Integer driverNumber) {

        final Optional<IdempotencyKey> optionalIdempotencyKey = idempotencyKeyRepository.findById(idempotencyKey);
        if (optionalIdempotencyKey.isPresent()) {
            logger.info(
                    "Idempotent request detected for idempotencyKey={}, returning previous resultId={}",
                    idempotencyKey,
                    optionalIdempotencyKey.get().getResultId());
            return buildGenericResponse(optionalIdempotencyKey.get().getResultId());
        }

        final SessionResponse sessionResponse = eventCache.getIfPresent(eventId.toString());
        if (sessionResponse == null) {
            throw new OddsExpiredException("Odds expired");
        }

        final User user =
                userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Balance is insufficient");
        }

        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);

        final Optional<DriverResponse> response = sessionResponse.getDrivers().stream()
                .filter(d -> Objects.equals(driverNumber, d.getNumber()))
                .findFirst();

        final DriverResponse driverResponse = response.orElseThrow(() -> new OddsNotFoundException("Odds not found"));

        final Bet bet = betRepository.save(new Bet(
                amount,
                Bet.BetStatus.PENDING,
                user,
                sessionResponse.getSessionKey(),
                driverResponse.getNumber(),
                driverResponse.getOdd()));

        idempotencyKeyRepository.save(new IdempotencyKey(idempotencyKey, userId, bet.getId()));

        logger.info(
                "Bet successfully created: betId={}, userId={}, driverNumber={}", bet.getId(), userId, driverNumber);
        return buildGenericResponse(bet.getId());
    }

    private GenericResponse buildGenericResponse(final UUID betId) {
        return new GenericResponse(betId);
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }

    public static class OddsExpiredException extends RuntimeException {
        public OddsExpiredException(String message) {
            super(message);
        }
    }

    public static class OddsNotFoundException extends RuntimeException {
        public OddsNotFoundException(String message) {
            super(message);
        }
    }
}
