package com.sporty.f1bet.infrastructure.provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProviderFactoryTest {

    @Autowired
    private ProviderFactory subject;

    @ParameterizedTest
    @ValueSource(strings = {"openF1Provider", "dbProvider"})
    @DisplayName("when a valid provider is provided, returns provider bean")
    void shouldReturnValidProvider(String provider) {
        assertNotNull(subject.getProvider(provider));
    }

    @Test
    @DisplayName("when an invalid provider is provided, throws InvalidProviderException")
    void throwInvalidProviderException() {
        assertThrows(ProviderFactory.InvalidProviderException.class, () -> {
            subject.getProvider("an-invalid-provider");
        });
    }
}
