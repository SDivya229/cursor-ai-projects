package com.emailservice.metrics;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe class for tracking email service metrics.
 * Uses atomic counters to ensure accurate counting in concurrent scenarios.
 */
@Slf4j
public class EmailMetrics implements AutoCloseable {
    private final AtomicLong successfulSends;
    private final AtomicLong failedAttempts;
    private final EmailMetricsConfig config;

    /**
     * Creates a new EmailMetrics instance with the specified configuration.
     * @param config The metrics configuration
     */
    public EmailMetrics(EmailMetricsConfig config) {
        this.config = config;
        this.successfulSends = new AtomicLong(0);
        this.failedAttempts = new AtomicLong(0);
    }

    /**
     * Records a successful email send.
     * @return The new count after recording
     */
    public long recordSuccessfulSend() {
        long count = successfulSends.incrementAndGet();
        if (config.isEnableDetailedLogging()) {
            log.debug("Successful sends count: {}", count);
        }
        return count;
    }

    /**
     * Records a failed email attempt.
     * @return The new count after recording
     */
    public long recordFailedAttempt() {
        long count = failedAttempts.incrementAndGet();
        if (config.isEnableDetailedLogging()) {
            log.debug("Failed attempts count: {}", count);
        }
        return count;
    }

    /**
     * Gets the current count of successful sends.
     * @return The number of successful email sends
     */
    public long getSuccessfulSends() {
        return successfulSends.get();
    }

    /**
     * Gets the current count of failed attempts.
     * @return The number of failed email attempts
     */
    public long getFailedAttempts() {
        return failedAttempts.get();
    }

    @Override
    public void close() {
        log.info("Final metrics - Successful: {}, Failed: {}", 
                getSuccessfulSends(), getFailedAttempts());
    }
} 