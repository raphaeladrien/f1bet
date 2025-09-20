package com.sporty.f1bet.provider;

import static org.springframework.http.HttpMethod.GET;

import com.sporty.f1bet.entity.Driver;
import com.sporty.f1bet.entity.Session;
import com.sporty.f1bet.mapper.DriverMapper;
import com.sporty.f1bet.mapper.SessionMapper;
import com.sporty.f1bet.provider.dto.DriverDTO;
import com.sporty.f1bet.provider.dto.SessionDTO;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OpenF1Provider implements Provider {

    private static final String SESSION_RESOURCE = "/sessions";
    private static final String DRIVER_RESOURCE = "/drivers";

    private final String url;
    private final RestTemplate restTemplate;

    public OpenF1Provider(@Value("${provider.openf1.url}") final String url, final RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    @Retryable(
            retryFor = {RestClientException.class},
            maxAttempts = 4,
            backoff =
                    @Backoff(
                            delayExpression = "${retry.backoff.delay}",
                            multiplierExpression = "${retry.backoff.multiplier}"))
    public List<Session> getSessions(final String sessionType, final Integer year, final String country) {
        if (year != null && year <= 0) {
            throw new InvalidParameterException("Year could not be <= 0");
        }

        final String uri = UriComponentsBuilder.fromUriString(url + SESSION_RESOURCE)
                .queryParamIfPresent("session_type", Optional.ofNullable(sessionType))
                .queryParamIfPresent("year", Optional.ofNullable(year))
                .queryParamIfPresent("country_code", Optional.ofNullable(country))
                .build()
                .encode()
                .toUri()
                .toString();

        final ResponseEntity<List<SessionDTO>> response =
                restTemplate.exchange(uri, GET, null, new ParameterizedTypeReference<>() {});

        if (response.getBody() == null) return Collections.emptyList();

        return response.getBody().stream().map(SessionMapper::toEntity).toList();
    }

    @Override
    @Retryable(
            retryFor = {RestClientException.class},
            maxAttempts = 4,
            backoff =
                    @Backoff(
                            delayExpression = "${retry.backoff.delay}",
                            multiplierExpression = "${retry.backoff.multiplier}"))
    public List<Driver> getDrivers(Integer sessionKey) {
        final String uri = UriComponentsBuilder.fromUriString(url + DRIVER_RESOURCE)
                .queryParam("session_key", sessionKey)
                .build()
                .encode()
                .toUri()
                .toString();

        final ResponseEntity<List<DriverDTO>> response =
                restTemplate.exchange(uri, GET, null, new ParameterizedTypeReference<>() {});

        if (response.getBody() == null) return Collections.emptyList();

        return response.getBody().stream().map(DriverMapper::toEntity).toList();
    }
}
