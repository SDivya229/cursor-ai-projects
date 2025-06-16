package com.emailservice.metrics;

import lombok.Builder;
import lombok.Getter;

/**
 * Configuration class for email metrics settings.
 * Provides configuration options for metrics collection.
 */
@Getter
@Builder
public class EmailMetricsConfig {
    @Builder.Default
    private final boolean enableDetailedLogging = false;
    
    @Builder.Default
    private final long metricsFlushInterval = 1000;  // milliseconds
    
    /**
     * Creates a default configuration for metrics.
     * @return EmailMetricsConfig instance with default settings
     */
    public static EmailMetricsConfig defaultConfig() {
        return EmailMetricsConfig.builder().build();
    }
} 