package com.emailservice;

import com.emailservice.config.EmailConfig;
import com.emailservice.config.EmailServiceConfig;
import com.emailservice.service.EmailService;
import com.emailservice.service.IEmailService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Demonstration class showing how to use the EmailService.
 */
public class EmailServiceDemo {
    public static void main(String[] args) {
        // Create email configuration
        EmailConfig emailConfig = EmailConfig.builder()
                .smtpHost("smtp.gmail.com")
                .smtpPort(587)
                .username("your.email@gmail.com")  // Replace with your email
                .password("your-app-password")     // Replace with your app password
                .enableTls(true)
                .build();

        // Create service configuration with custom settings
        EmailServiceConfig serviceConfig = EmailServiceConfig.builder()
                .emailConfig(emailConfig)
                .threadPoolSize(10)
                .timeoutMillis(45000)  // 45 seconds
                .maxRetries(3)
                .build();

        // Create email service
        try (IEmailService emailService = new EmailService(serviceConfig)) {
            // Send multiple test emails
            CompletableFuture<String> future1 = emailService.sendEmailAsync(
                    "sender@example.com",
                    "recipient1@example.com",
                    "Test Email 1",
                    "This is test email 1."
            );

            CompletableFuture<String> future2 = emailService.sendEmailAsync(
                    "sender@example.com",
                    "recipient2@example.com",
                    "Test Email 2",
                    "This is test email 2."
            );

            // Wait for both emails to complete
            CompletableFuture.allOf(future1, future2)
                    .thenRun(() -> {
                        try {
                            // Get and display metrics
                            long sentCount = emailService.getEmailsSentCount().get(5, TimeUnit.SECONDS);
                            long failedCount = emailService.getFailedAttemptsCount().get(5, TimeUnit.SECONDS);
                            
                            System.out.println("\nEmail Service Metrics:");
                            System.out.println("Successful Sends: " + sentCount);
                            System.out.println("Failed Attempts: " + failedCount);
                        } catch (Exception e) {
                            System.err.println("Error getting metrics: " + e.getMessage());
                        }
                    })
                    .get(1, TimeUnit.MINUTES);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
} 