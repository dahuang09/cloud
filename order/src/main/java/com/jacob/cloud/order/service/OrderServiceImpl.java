package com.jacob.cloud.order.service;

import com.jacob.cloud.itemapi.entity.Item;
import com.jacob.cloud.itemapi.feign.ItemClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.function.Supplier;

@Service(value = "orderServiceImpl")
@Retry(name = "retryBackendA")
//@CircuitBreaker(name = "backendA")
//@RateLimiter(name = "backendRateLimiter")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemClient itemClient;

    @Override
    public String subItem() {
        Item item;

        CircuitBreaker circuitBreaker = createCircuitBreaker();
        Supplier<Item> circuitSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, () -> itemClient.item());

        RateLimiter rateLimiter = createRateLimiter();
        Supplier<Item> rateSupplier = RateLimiter.decorateSupplier(rateLimiter, () -> itemClient.item());

        Item itemRecover = new Item();
        itemRecover.setItemId("服务错误");

        Try<Item> recover = Try.ofSupplier(circuitSupplier).recover(Exception.class, itemRecover);
        item = recover.get();

        System.out.println((item == null ? null : item.getItemId()));

        return "sub items =" + item.getItemId();
    }

    private CircuitBreaker createCircuitBreaker() {
        CircuitBreakerConfig cfg = CircuitBreakerConfig
                .custom()
                .failureRateThreshold(50) // 错误率，这个是根据滑动窗口大小决定的，e.g. windowSize = 2，failureRate=50% 那么，当出现一个错误的时候即为失败
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .permittedNumberOfCallsInHalfOpenState(2)
                .slidingWindowSize(2) // 滑动窗口大小
                .recordExceptions(RuntimeException.class) // 当出现列表中的异常类型时记录
                .build();
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(cfg);
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendA", cfg);

        return circuitBreaker;
    }

    private RateLimiter createRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMillis(1000))
                .limitForPeriod(1)
                .timeoutDuration(Duration.ofMillis(1000))
                .build();
        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("backendRateLimiter");

        return rateLimiter;
    }
}
