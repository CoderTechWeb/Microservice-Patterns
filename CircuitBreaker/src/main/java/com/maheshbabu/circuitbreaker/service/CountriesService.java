package com.maheshbabu.circuitbreaker.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CountriesService {

    private final RestTemplate restTemplate;

    public CountriesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 🎯 Method with Circuit Breaker
    @CircuitBreaker(name = "countriesCircuitBreaker", fallbackMethod = "circuitBreakerFallback")
    public List<Object> getCountriesWithCircuitBreaker() throws Exception {
        System.out.println("CircuitBreaker - Processing request at " + Instant.now());
        throw new Exception("Simulated API failure!"); // Force failure
        //throw new FileNotFoundException("Simulated API failure!"); // Force failure
        //return new ArrayList<>();
    }

    public List<Object> circuitBreakerFallback(Throwable t) {
        return List.of("Circuit Breaker Open! Please try again later.");
    }

    // 🎯 Method with Retry
    @Retry(name = "countriesRetry", fallbackMethod = "retryFallback")
    public List<Object> getCountriesWithRetry() throws Exception {
        System.out.println("Retry - Processing request at " + Instant.now());
        throw new FileNotFoundException("Simulated API failure!"); // Force failure
    }

    public List<Object> retryFallback(Throwable t) {
        return List.of("Retry exhausted! Service temporarily unavailable.");
    }

    @Bulkhead(name = "countriesBulkhead", fallbackMethod = "bulkheadFallback")
    public List<Object> getCountriesBulkHead() throws Exception {
        System.out.println(Thread.currentThread().getName() + " - Processing request");
        Thread.sleep(3000); // Simulating a slow request
        Object[] countries = restTemplate.getForObject("https://restcountries.com/v3.1/all", Object[].class);
        return Arrays.stream(countries).toList().subList(1, 10);
    }

    public List<Object> bulkheadFallback(Throwable t) {
        return List.of("Bulkhead limit exceeded! Please try again later.");
    }

    @RateLimiter(name = "countriesRateLimiter", fallbackMethod = "rateLimiterFallback")
    public List<Object> getCountriesRateLimiter() throws Exception {
        System.out.println(Thread.currentThread().getName() + " - Processing request at " + Instant.now());
        Object[] countries = restTemplate.getForObject("https://restcountries.com/v3.1/all", Object[].class);
        return Arrays.stream(countries).toList().subList(1, 10);
    }

    public List<Object> rateLimiterFallback(Throwable t) {
        return List.of("Rate limit exceeded! Please try again later.");
    }

    @CircuitBreaker(name = "combinedCircuitBreaker", fallbackMethod = "circuitBreakerFallback")
    @Retry(name = "combinedRetry", fallbackMethod = "retryFallback")
    @Bulkhead(name = "combinedBulkhead", fallbackMethod = "bulkheadFallback")
    @RateLimiter(name = "combinedRateLimiter", fallbackMethod = "rateLimiterFallback")
    public List<Object> getCountriesWithAllPatterns() throws Exception {
        System.out.println("Processing request at " + Instant.now() + " - Thread: " + Thread.currentThread().getName());
        throw new Exception("Simulated API failure!"); // Force failure
    }


}
