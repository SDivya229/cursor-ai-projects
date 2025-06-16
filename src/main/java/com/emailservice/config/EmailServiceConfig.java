package com.emailservice.config;

import lombok.Builder;
import lombok.Getter;

/**
 * Configuration class for email service operational settings.
 * Contains thread pool, timeout, and retry configurations.
 */
@Getter
@Builder
public class EmailServiceConfig {
    @Builder.Default
    private final int threadPoolSize = 5;
    
    @Builder.Default
    private final long timeoutMillis = 30000; // 30 seconds default
    
    @Builder.Default
    private final int maxRetries = 3;
    
    private final EmailConfig emailConfig;

    /**
     * Creates a default configuration with standard settings.
     * @param emailConfig The email server configuration
     * @return EmailServiceConfig instance with default settings
     */
    public static EmailServiceConfig defaultConfig(EmailConfig emailConfig) {
        return EmailServiceConfig.builder()
                .emailConfig(emailConfig)
                .build();
    }
} 