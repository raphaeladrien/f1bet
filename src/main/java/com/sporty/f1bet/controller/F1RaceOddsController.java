package com.sporty.f1bet.controller;

import com.sporty.f1bet.controller.dto.OddRequest;
import com.sporty.f1bet.dto.BetOptionsResponse;
import com.sporty.f1bet.dto.ProcessBetResponse;
import com.sporty.f1bet.interactors.ProcessBet;
import com.sporty.f1bet.interactors.RetrieveBetOptions;
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

    @GetMapping("/odds")
    public ResponseEntity<BetOptionsResponse> sessions(
            @RequestParam(required = false) String sessionType,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(betOptions.execute(sessionType, year, country, page, size));
    }

    @PostMapping("/odds")
    public ResponseEntity<ProcessBetResponse> odds(
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
