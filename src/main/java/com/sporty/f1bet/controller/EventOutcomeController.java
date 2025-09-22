package com.sporty.f1bet.controller;

import com.sporty.f1bet.controller.dto.EventOutcomeRequest;
import com.sporty.f1bet.controller.exception.ApiError;
import com.sporty.f1bet.dto.GenericResponse;
import com.sporty.f1bet.interactors.SaveEventOutcome;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class EventOutcomeController {

    private final SaveEventOutcome eventOutcome;

    public EventOutcomeController(SaveEventOutcome eventOutcome) {
        this.eventOutcome = eventOutcome;
    }

    @PostMapping("/events/result")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Operation completed successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = GenericResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiError.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiError.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access forbidden",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiError.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiError.class)))
            })
    public ResponseEntity<GenericResponse> saveResult(
            @RequestBody final EventOutcomeRequest request,
            @RequestHeader(value = "Idempotency-Key") final String idempotencyKey) {
        return ResponseEntity.ok(eventOutcome.execute(
                request.sessionKey(), request.winningNumber(), request.userId(), UUID.fromString(idempotencyKey)));
    }
}
