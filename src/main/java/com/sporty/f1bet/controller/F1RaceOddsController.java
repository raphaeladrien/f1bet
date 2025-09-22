package com.sporty.f1bet.controller;

import com.sporty.f1bet.controller.dto.OddRequest;
import com.sporty.f1bet.controller.exception.ApiError;
import com.sporty.f1bet.dto.BetOptionsResponse;
import com.sporty.f1bet.dto.GenericResponse;
import com.sporty.f1bet.interactors.ProcessBet;
import com.sporty.f1bet.interactors.RetrieveBetOptions;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class F1RaceOddsController {

    private final RetrieveBetOptions betOptions;
    private final ProcessBet processBet;

    public F1RaceOddsController(final RetrieveBetOptions betOptions, final ProcessBet processBet) {
        this.betOptions = betOptions;
        this.processBet = processBet;
    }

    @GetMapping("/events")
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
                        responseCode = "400",
                        description = "Bad request",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiError.class)))
            })
    public ResponseEntity<BetOptionsResponse> sessions(
            @RequestParam(required = false) String sessionType,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(betOptions.execute(sessionType, year, country, page, size));
    }

    @PostMapping("/odds")
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
                        responseCode = "410",
                        description = "Betting odds are not available",
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
                        responseCode = "402",
                        description = "Insufficient funds",
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
    public ResponseEntity<GenericResponse> odds(
            @RequestBody final OddRequest request,
            @RequestHeader(value = "Idempotency-Key") final String idempotencyKey) {
        return ResponseEntity.ok(processBet.execute(
                request.userId(),
                request.amount(),
                UUID.fromString(idempotencyKey),
                request.eventId(),
                request.driverNumber()));
    }
}
