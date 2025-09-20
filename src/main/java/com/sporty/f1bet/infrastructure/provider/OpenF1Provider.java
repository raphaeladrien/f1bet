package com.sporty.f1bet.infrastructure.provider;

import static org.springframework.http.HttpMethod.GET;

import com.sporty.f1bet.application.provider.Provider;
import com.sporty.f1bet.infrastructure.mapper.SessionMapper;
import com.sporty.f1bet.infrastructure.persistence.entity.Driver;
import com.sporty.f1bet.infrastructure.persistence.entity.Session;
import com.sporty.f1bet.infrastructure.provider.dto.SessionDTO;
import java.util.Collections;
import java.util.List;
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
            backoff = @Backoff(delay = 1000, multiplier = 2.0))
    public List<Session> getSessions() {
        final ResponseEntity<List<SessionDTO>> response = restTemplate.exchange(
                UriComponentsBuilder.fromUriString(url + SESSION_RESOURCE).toUriString(),
                GET,
                null,
                new ParameterizedTypeReference<>() {});

        if (response.getBody() == null) return Collections.emptyList();

        return response.getBody().stream().map(SessionMapper::toEntity).toList();
    }

    @Override
    public List<Driver> getDrivers(Integer sessionKey) {
        return List.of();
    }
}
