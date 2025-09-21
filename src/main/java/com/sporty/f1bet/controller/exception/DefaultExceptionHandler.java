package com.sporty.f1bet.controller.exception;

import static org.springframework.http.HttpStatus.*;

import com.sporty.f1bet.interactors.ProcessBet;
import com.sporty.f1bet.interactors.SaveEventOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(ProcessBet.OddsExpiredException.class)
    public ResponseEntity<ApiError> handleOddsExpiredException(ProcessBet.OddsExpiredException ex) {
        final String title = "Betting odds are not available";
        final int status = GONE.value();
        final String detail =
                "The odds for the selected driver/session are not available at this time. Remember: odds are only "
                        + "valid for 3 minutes. Please try again later or choose a different bet.";
        final String instance = "/api/v1/odds";

        if (logger.isErrorEnabled()) logger.error(ex.getMessage(), ex);

        return ResponseEntity.status(GONE).body(new ApiError(title, status, detail, instance));
    }

    @ExceptionHandler(ProcessBet.InsufficientFundsException.class)
    public ResponseEntity<ApiError> handleInsufficientFundsException(ProcessBet.InsufficientFundsException ex) {
        final String title = "Insufficient funds";
        final int status = PAYMENT_REQUIRED.value();
        final String detail = "You do not have enough funds to place this bet. Please deposit additional funds "
                + "or reduce your bet amount.";
        final String instance = "/api/v1/odds";

        if (logger.isErrorEnabled()) logger.error(ex.getMessage(), ex);

        return ResponseEntity.status(PAYMENT_REQUIRED).body(new ApiError(title, status, detail, instance));
    }

    @ExceptionHandler(ProcessBet.UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(ProcessBet.UserNotFoundException ex) {
        final String title = "User not found";
        final int status = NOT_FOUND.value();
        final String detail = "The user associated does not exists or has been removed";
        final String instance = "/api/v1/odds";

        if (logger.isErrorEnabled()) logger.error(ex.getMessage(), ex);

        return ResponseEntity.status(NOT_FOUND).body(new ApiError(title, status, detail, instance));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex) {
        final String title = "Internal server error";
        final int status = INTERNAL_SERVER_ERROR.value();
        final String detail = "An unexpected error occurred while processing your request. Please try again later";
        final String instance = "/api/v1";

        if (logger.isErrorEnabled()) logger.error(ex.getMessage(), ex);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiError(title, status, detail, instance));
    }

    @ExceptionHandler(SaveEventOutcome.UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(SaveEventOutcome.UserNotFoundException ex) {
        final String title = "User not found";
        final int status = NOT_FOUND.value();
        final String detail = "The user associated does not exists or has been removed";
        final String instance = "/api/v1/event/result";

        if (logger.isErrorEnabled()) logger.error(ex.getMessage(), ex);

        return ResponseEntity.status(NOT_FOUND).body(new ApiError(title, status, detail, instance));
    }

    @ExceptionHandler(SaveEventOutcome.InvalidUserException.class)
    public ResponseEntity<ApiError> handleInvalidUserException(SaveEventOutcome.InvalidUserException ex) {
        final String title = "Access forbidden";
        final int status = FORBIDDEN.value();
        final String detail = "You do not have permission to perform this action";
        final String instance = "/api/v1/event/result";

        if (logger.isErrorEnabled()) logger.error(ex.getMessage(), ex);

        return ResponseEntity.status(FORBIDDEN).body(new ApiError(title, status, detail, instance));
    }
}
