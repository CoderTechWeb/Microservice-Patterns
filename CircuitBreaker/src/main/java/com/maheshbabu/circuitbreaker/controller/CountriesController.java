package com.maheshbabu.circuitbreaker.controller;

import com.maheshbabu.circuitbreaker.service.CountriesService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CountriesController {


    private final CountriesService countriesService;

    public CountriesController(CountriesService countriesService) {
        this.countriesService = countriesService;
    }

    @GetMapping("/countries")
    public List<Object> getCountriesCircuitBreaker() throws Exception {
        return countriesService.getCountriesWithCircuitBreaker();
    }

    @GetMapping("/countriesbulk")
    public List<Object> getCountriesbulk() throws Exception {
        return countriesService.getCountriesBulkHead();
    }

    @GetMapping("/countriesrate")
    public List<Object> getCountriesrate() throws Exception {
        return countriesService.getCountriesRateLimiter();
    }

    public List<Object> getCountries(Throwable throwable) {
        List<Object> countries = new ArrayList<>();
        countries.add("Country service unavailable!");
        return countries;
    }

}
