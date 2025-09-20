package com.sporty.f1bet.infrastructure.provider;

import com.sporty.f1bet.application.provider.Provider;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ProviderFactory {

    private final Map<String, Provider> providers;

    public ProviderFactory(Map<String, Provider> providers) {
        this.providers = providers;
    }

    public Provider getProvider(String name) {
        final Provider provider = providers.get(name);
        if (provider == null) {
            throw new InvalidProviderException("Invalid provider name was provided");
        }
        return provider;
    }

    public static class InvalidProviderException extends IllegalArgumentException {
        public InvalidProviderException(final String message) {
            super(message);
        }
    }
}
