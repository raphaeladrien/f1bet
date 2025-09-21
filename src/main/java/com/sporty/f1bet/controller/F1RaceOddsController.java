package com.sporty.f1bet.controller;

import com.sporty.f1bet.dto.BetOptionsResponse;
import com.sporty.f1bet.interactors.RetrieveBetOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class F1RaceOddsController {
    @Autowired
    private RetrieveBetOptions betOptions;

    @GetMapping("/odds")
    public ResponseEntity<BetOptionsResponse> sessions(
            @RequestParam(required = false) String sessionType,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(betOptions.execute(sessionType, year, country, page, size));
    }
}
