package com.sporty.f1bet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BetOptionsResponse {

    @JsonProperty("event")
    private final List<SessionResponse> sessionResponse;

    public BetOptionsResponse(List<SessionResponse> sessionResponse) {
        this.sessionResponse = sessionResponse;
    }

    public List<SessionResponse> getSessionResponse() {
        return sessionResponse;
    }
}
