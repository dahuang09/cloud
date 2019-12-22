package com.jacob.cloud.circuitbreaker.controller;

import com.jacob.cloud.circuitbreaker.service.BackendService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Supplier;

@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private BackendService backendService;

    @GetMapping("/test")
    public String test() {
        // Create a CircuitBreaker (use default configuration)
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("backendName");
// Create a Retry with at most 3 retries and a fixed time interval between retries of 500ms
        Retry retry = Retry.ofDefaults("backendName");

// Decorate your call to BackendService.doSomething() with a CircuitBreaker
        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, backendService::doSomething);

// Decorate your call with automatic retry
        decoratedSupplier = Retry
                .decorateSupplier(retry, decoratedSupplier);

// Execute the decorated supplier and recover from any exception
        String result = Try.ofSupplier(decoratedSupplier)
                .recover(throwable -> "Hello from Recovery").get();

// When you don't want to decorate your lambda expression,
// but just execute it and protect the call by a CircuitBreaker.
//        String result = circuitBreaker.executeSupplier(backendService::doSomething);
        return result;
    }

}
