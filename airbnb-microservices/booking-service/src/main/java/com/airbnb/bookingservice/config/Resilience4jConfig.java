package com.airbnb.bookingservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {
    private static final Logger logger = LoggerFactory.getLogger(Resilience4jConfig.class);

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig propertyServiceConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50.0f)
                .slowCallRateThreshold(50.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .waitDurationInOpenState(Duration.ofSeconds(5))
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(Exception.class)
                .ignoreExceptions(IllegalArgumentException.class)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(propertyServiceConfig);
        registry.getEventPublisher()
                .onEntryAdded(event -> logger.info("CircuitBreaker adaugat: {}", event.getAddedEntry().getName()))
                .onEntryRemoved(event -> logger.info("CircuitBreaker eliminat: {}", event.getRemovedEntry().getName()));

        return registry;
    }

    @Bean
    public RegistryEventConsumer<CircuitBreaker> circuitBreakerEventConsumer() {
        return new RegistryEventConsumer<CircuitBreaker>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> event) {
                CircuitBreaker circuitBreaker = event.getAddedEntry();
                logger.info("CircuitBreaker '{}' inregistrat", circuitBreaker.getName());
                circuitBreaker.getEventPublisher()
                        .onError(e -> logger.warn("CircuitBreaker '{}' a inregistrat o eroare", circuitBreaker.getName()))
                        .onStateTransition(e -> logger.warn("CircuitBreaker '{}' a trecut in starea {}",
                                circuitBreaker.getName(), e.getStateTransition().getToState()));
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> event) {
                logger.info("CircuitBreaker eliminat: {}", event.getRemovedEntry().getName());
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> event) {
                logger.info("CircuitBreaker inlocuit: {}", event.getNewEntry().getName());
            }
        };
    }
}
