package com.emailservice.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailMetrics class.
 */
class EmailMetricsTest {
    private EmailMetrics metrics;
    private EmailMetricsConfig config;

    @BeforeEach
    void setUp() {
        config = EmailMetricsConfig.builder()
                .enableDetailedLogging(true)
                .build();
        metrics = new EmailMetrics(config);
    }

    @Test
    void shouldIncrementSuccessfulSends() {
        assertEquals(0, metrics.getSuccessfulSends());
        metrics.recordSuccessfulSend();
        assertEquals(1, metrics.getSuccessfulSends());
    }

    @Test
    void shouldIncrementFailedAttempts() {
        assertEquals(0, metrics.getFailedAttempts());
        metrics.recordFailedAttempt();
        assertEquals(1, metrics.getFailedAttempts());
    }

    @Test
    void shouldHandleConcurrentIncrements() throws InterruptedException {
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    metrics.recordSuccessfulSend();
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(threadCount * 100, metrics.getSuccessfulSends());
    }

    @Test
    void shouldMaintainSeparateCounters() {
        metrics.recordSuccessfulSend();
        metrics.recordSuccessfulSend();
        metrics.recordFailedAttempt();

        assertEquals(2, metrics.getSuccessfulSends());
        assertEquals(1, metrics.getFailedAttempts());
    }
} 