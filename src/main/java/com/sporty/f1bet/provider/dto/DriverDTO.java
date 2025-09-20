package com.sporty.f1bet.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DriverDTO(
        @JsonProperty("session_key") Integer sessionKey,
        @JsonProperty("driver_number") Integer driverNumber,
        @JsonProperty("broadcast_name") String broadcastName,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("name_acronym") String nameAcronym,
        @JsonProperty("team_name") String teamName,
        @JsonProperty("team_colour") String teamColour,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("headshot_url") String headshotUrl,
        @JsonProperty("country_code") String countryCode) {}
