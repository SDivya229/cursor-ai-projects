package com.emailservice.config;

import lombok.Builder;
import lombok.Getter;

/**
 * Configuration class for email service settings.
 * Contains SMTP server details and authentication credentials.
 */
@Getter
@Builder
public class EmailConfig {
    private final String smtpHost;
    private final int smtpPort;
    private final String username;
    private final String password;
    private final boolean enableTls;

    /**
     * Creates a default configuration for Gmail SMTP.
     * @return EmailConfig instance with Gmail SMTP settings
     */
    public static EmailConfig defaultConfig() {
        return EmailConfig.builder()
                .smtpHost("smtp.gmail.com")
                .smtpPort(587)
                .enableTls(true)
                .build();
    }
} 