package com.sporty.f1bet.controller;

import com.sporty.f1bet.controller.dto.EventOutcomeRequest;
import com.sporty.f1bet.dto.GenericResponse;
import com.sporty.f1bet.interactors.SaveEventOutcome;
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

    @PostMapping("/event/result")
    public ResponseEntity<GenericResponse> saveResult(
            @RequestBody final EventOutcomeRequest request,
            @RequestHeader(value = "Idempotency-Key") final String idempotencyKey) {
        return ResponseEntity.ok(eventOutcome.execute(
                request.sessionKey(), request.winningNumber(), request.userId(), UUID.fromString(idempotencyKey)));
    }
}
