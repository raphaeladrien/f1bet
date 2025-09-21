package com.sporty.f1bet.interactors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.sporty.f1bet.dto.GenericResponse;
import com.sporty.f1bet.entity.EventOutcome;
import com.sporty.f1bet.entity.IdempotencyKey;
import com.sporty.f1bet.entity.User;
import com.sporty.f1bet.repository.EventOutcomeRepository;
import com.sporty.f1bet.repository.IdempotencyKeyRepository;
import com.sporty.f1bet.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SaveEventOutcomeTest {

    private final IdempotencyKeyRepository idempotencyKeyRepository = mock(IdempotencyKeyRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final EventOutcomeRepository eventOutcomeRepository = mock(EventOutcomeRepository.class);

    private SaveEventOutcome saveEventOutcome =
            new SaveEventOutcome(idempotencyKeyRepository, userRepository, eventOutcomeRepository);

    @Test
    @DisplayName("should throw UserNotFoundException when user does not exist")
    void shouldThrowUserNotFoundWhenUserDoesNotExist() {
        final Long userId = 1L;
        final UUID idempotencyKey = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                SaveEventOutcome.UserNotFoundException.class,
                () -> saveEventOutcome.execute(100, 7, userId, idempotencyKey));

        verifyNoInteractions(idempotencyKeyRepository, eventOutcomeRepository);
    }

    @Test
    @DisplayName("should throw InvalidUserException when user is not admin")
    void shouldThrowInvalidUserWhenUserIsNotAdmin() {
        final Long userId = 1L;
        final UUID idempotencyKey = UUID.randomUUID();
        final User user = buildUser(userId, false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(
                SaveEventOutcome.InvalidUserException.class,
                () -> saveEventOutcome.execute(200, 9, userId, idempotencyKey));

        verifyNoInteractions(idempotencyKeyRepository, eventOutcomeRepository);
    }

    @Test
    @DisplayName("should return existing result when idempotency key already exists")
    void shouldReturnExistingResultWhenIdempotencyKeyExists() {
        final Long userId = 1L;
        final UUID idempotencyKey = UUID.randomUUID();
        final UUID existingResultId = UUID.randomUUID();

        final User user = buildUser(userId, true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(idempotencyKeyRepository.findById(idempotencyKey))
                .thenReturn(Optional.of(new IdempotencyKey(idempotencyKey, userId, existingResultId)));

        final GenericResponse response = saveEventOutcome.execute(300, 11, userId, idempotencyKey);

        assertEquals(existingResultId, response.requestId());

        verify(idempotencyKeyRepository).findById(idempotencyKey);
        verifyNoInteractions(eventOutcomeRepository);
    }

    @Test
    @DisplayName(
            "should save event outcome and return new response when user is admin and idempotency key does not exist")
    void shouldSaveEventOutcomeAndReturnNewResponse() {
        final Long userId = 1L;
        final UUID idempotencyKey = UUID.randomUUID();
        final UUID newEventId = UUID.randomUUID();

        final User user = buildUser(userId, true);
        final EventOutcome eventOutcome = new EventOutcome(400, 33, user);
        eventOutcome.setId(newEventId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(idempotencyKeyRepository.findById(idempotencyKey)).thenReturn(Optional.empty());
        when(eventOutcomeRepository.save(any(EventOutcome.class))).thenReturn(eventOutcome);

        GenericResponse response = saveEventOutcome.execute(400, 33, userId, idempotencyKey);

        assertEquals(newEventId, response.requestId());

        verify(eventOutcomeRepository).save(any(EventOutcome.class));
    }

    private User buildUser(Long id, boolean admin) {
        final User user = new User();
        user.setId(id);
        user.setRole(admin ? User.UserRole.ADMIN : User.UserRole.USER);
        return user;
    }
}
