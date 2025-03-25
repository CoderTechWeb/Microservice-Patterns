package com.techweb.apigateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreakerConfigCustomizer orderServiceCircuitBreaker() {
        return CircuitBreakerConfigCustomizer.of("orderServiceCB",
                builder -> builder
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(5))
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .recordExceptions(RuntimeException.class, Exception.class) // Ensures fallbacks count
                        .ignoreExceptions(IllegalArgumentException.class) // (Optional) Ignore
        );
    }

    @Bean
    public CircuitBreakerConfigCustomizer paymentServiceCircuitBreaker() {
        return CircuitBreakerConfigCustomizer.of("paymentServiceCB",
            builder -> builder
                    .failureRateThreshold(50)
                    .waitDurationInOpenState(Duration.ofSeconds(5))
                    .slidingWindowSize(10)
                    .minimumNumberOfCalls(5)
                    .recordExceptions(Exception.class)
        );
    }


}
