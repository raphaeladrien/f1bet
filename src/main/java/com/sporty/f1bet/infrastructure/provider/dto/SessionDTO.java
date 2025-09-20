package com.sporty.f1bet.infrastructure.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record SessionDTO(
        @JsonProperty("circuit_key") Integer circuitKey,
        @JsonProperty("circuit_short_name") String circuitShortName,
        @JsonProperty("country_code") String countryCode,
        @JsonProperty("country_key") Integer countryKey,
        @JsonProperty("country_name") String countryName,
        @JsonProperty("date_end") OffsetDateTime dateEnd,
        @JsonProperty("date_start") OffsetDateTime dateStart,
        @JsonProperty("gmt_offset") String gmtOffset,
        String location,
        @JsonProperty("session_name") String sessionName,
        @JsonProperty("session_key") Integer sessionKey,
        @JsonProperty("session_type") String sessionType,
        Integer year) {}
