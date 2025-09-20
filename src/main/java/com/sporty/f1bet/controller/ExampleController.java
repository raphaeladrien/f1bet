package com.sporty.f1bet.controller;

import com.sporty.f1bet.dto.BetOptionsResponse;
import com.sporty.f1bet.interactors.RetrieveBetOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {
    @Autowired
    private RetrieveBetOptions betOptions;

    @GetMapping("/hello")
    public ResponseEntity<BetOptionsResponse> sessions() {
        return ResponseEntity.ok(betOptions.execute("Race", 2023, "BRA"));
    }
}
