package com.sporty.f1bet.interactors;

import com.sporty.f1bet.dto.GenericResponse;
import com.sporty.f1bet.entity.EventOutcome;
import com.sporty.f1bet.entity.IdempotencyKey;
import com.sporty.f1bet.entity.User;
import com.sporty.f1bet.repository.EventOutcomeRepository;
import com.sporty.f1bet.repository.IdempotencyKeyRepository;
import com.sporty.f1bet.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SaveEventOutcome {

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final UserRepository userRepository;
    private final EventOutcomeRepository eventOutcomeRepository;

    public SaveEventOutcome(
            IdempotencyKeyRepository idempotencyKeyRepository,
            UserRepository userRepository,
            EventOutcomeRepository eventOutcomeRepository) {
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.userRepository = userRepository;
        this.eventOutcomeRepository = eventOutcomeRepository;
    }

    public GenericResponse execute(Integer sessionKey, Integer winningNumber, Long userId, UUID idempotencyKey) {
        final Optional<User> optUser = userRepository.findById(userId);
        final User user = optUser.orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getRole().isAdmin()) throw new InvalidUserException("You don't have permission to do this operation");

        final Optional<IdempotencyKey> optionalIdempotencyKey = idempotencyKeyRepository.findById(idempotencyKey);
        if (optionalIdempotencyKey.isPresent()) {
            return buildGenericResponse(optionalIdempotencyKey.get().getResultId());
        }

        final EventOutcome event = eventOutcomeRepository.save(new EventOutcome(sessionKey, winningNumber, user));

        return new GenericResponse(event.getId());
    }

    private GenericResponse buildGenericResponse(final UUID betId) {
        return new GenericResponse(betId);
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String mensagem) {
            super(mensagem);
        }
    }

    public static class InvalidUserException extends RuntimeException {
        public InvalidUserException(String mensagem) {
            super(mensagem);
        }
    }
}
