package com.maheshbabu.circuitbreaker.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootTest
public class ResiliencePatternsTest {

    @Autowired
    private CountriesService countriesService;

    // 🎯 Test Circuit Breaker: Failures should trigger CB fallback
    @Test
    void testCircuitBreaker() throws InterruptedException {
        for (int i = 1; i <= 10; i++) {
            try {
                System.out.println("Attempt " + i + ": " + countriesService.getCountriesWithCircuitBreaker());
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            Thread.sleep(500); // Delay between requests
        }

        Thread.sleep(10000);

        for (int i = 1; i <= 4; i++) {
            try {
                System.out.println("Attempt " + i + ": " + countriesService.getCountriesWithCircuitBreaker());
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            Thread.sleep(500); // Delay between requests
        }
    }

    // 🎯 Test Retry: Should retry 3 times before failing
    @Test
    void testRetry() {
        try {
            System.out.println("Response: " + countriesService.getCountriesWithRetry());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    // 🎯 Test Bulkhead: Should tigger after 2 times
    @Test
    void testBulkhead() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5); // 5 parallel threads

        List<Future<List<Object>>> futures = new ArrayList<>();

        // Simulate 5 parallel requests
        for (int i = 0; i < 5; i++) {
            futures.add(executor.submit(() -> {
                try {
                    return countriesService.getCountriesBulkHead();
                } catch (Exception e) {
                    return List.of("Exception occurred!");
                }
            }));
        }

        // Print the results
        for (Future<List<Object>> future : futures) {
            try {
                System.out.println(future.get());  // Get response from each request
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }

    // 🎯 Test Ratelimiter: Should tigger ratelimit after 3 call and refresh after 20 second
    @Test
    void testRateLimiter() throws InterruptedException {
        for (int i = 1; i <= 20; i++) {
            try {
                System.out.println("Request " + i + ": " + countriesService.getCountriesRateLimiter());
            } catch (Exception e) {
                System.out.println("Request " + i + ": " + e.getMessage());
            }
            Thread.sleep(1000); // 1s delay between requests
        }
    }

    // 🎯 Test all patterns together
    @Test
    void testAllPatterns() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<List<Object>>> results = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            results.add(executor.submit(() -> {
                try {
                    return countriesService.getCountriesWithAllPatterns();
                } catch (Exception e) {
                    return List.of("Exception: " + e.getMessage());
                }
            }));
            Thread.sleep(500); // Delay to simulate API rate
        }

        for (Future<List<Object>> future : results) {
            try {
                System.out.println("Response: " + future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }
}
