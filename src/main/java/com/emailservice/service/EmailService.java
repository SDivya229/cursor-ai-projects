package com.emailservice.service;

import com.emailservice.config.EmailServiceConfig;
import com.emailservice.exception.EmailServiceException;
import com.emailservice.message.EmailMessageBuilder;
import com.emailservice.metrics.EmailMetrics;
import com.emailservice.metrics.EmailMetricsConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Implementation of IEmailService that provides asynchronous email sending capabilities.
 * This service uses JavaMail API and follows Java best practices for async operations.
 */
@Slf4j
public class EmailService implements IEmailService {
    private final EmailServiceConfig config;
    private final ExecutorService executorService;
    private final Session session;
    private final EmailMetrics metrics;

    /**
     * Constructs a new EmailService with the specified configuration.
     * @param config Service configuration containing all necessary settings
     */
    public EmailService(EmailServiceConfig config) {
        this.config = config;
        this.executorService = Executors.newFixedThreadPool(config.getThreadPoolSize());
        this.session = createSession();
        this.metrics = new EmailMetrics(EmailMetricsConfig.defaultConfig());
    }

    @Override
    public CompletableFuture<String> sendEmailAsync(String from, String to, String subject, String body) {
        return withRetry(() -> {
            try {
                validateEmailAddress(from);
                validateEmailAddress(to);
                
                Message message = new EmailMessageBuilder(session)
                        .from(from)
                        .to(to)
                        .subject(subject)
                        .body(body)
                        .build();

                Transport.send(message);
                metrics.recordSuccessfulSend();
                log.info("Email sent successfully to: {}", to);
                return "Email sent successfully!";
            } catch (MessagingException e) {
                metrics.recordFailedAttempt();
                log.error("Failed to send email", e);
                throw new EmailServiceException("Failed to send email", e);
            }
        });
    }

    @Override
    public CompletableFuture<Long> getEmailsSentCount() {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    return metrics.getSuccessfulSends();
                } catch (Exception e) {
                    log.error("Failed to get successful sends count", e);
                    throw new EmailServiceException("Failed to get metrics", e);
                }
            },
            executorService
        );
    }

    @Override
    public CompletableFuture<Long> getFailedAttemptsCount() {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    return metrics.getFailedAttempts();
                } catch (Exception e) {
                    log.error("Failed to get failed attempts count", e);
                    throw new EmailServiceException("Failed to get metrics", e);
                }
            },
            executorService
        );
    }

    /**
     * Creates and configures a JavaMail Session with the provided configuration.
     * @return Configured Session instance
     */
    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.getEmailConfig().getSmtpHost());
        props.put("mail.smtp.port", config.getEmailConfig().getSmtpPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", config.getEmailConfig().isEnableTls());

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    config.getEmailConfig().getUsername(),
                    config.getEmailConfig().getPassword()
                );
            }
        });
    }

    /**
     * Executes an operation with retry logic.
     * @param operation The operation to execute
     * @return CompletableFuture containing the operation result
     */
    private CompletableFuture<String> withRetry(Supplier<String> operation) {
        return CompletableFuture.supplyAsync(() -> {
            Exception lastException = null;
            for (int i = 0; i < config.getMaxRetries(); i++) {
                try {
                    return operation.get();
                } catch (Exception e) {
                    lastException = e;
                    log.warn("Retry {} failed", i + 1, e);
                    if (i < config.getMaxRetries() - 1) {
                        try {
                            Thread.sleep((i + 1) * 1000L);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new EmailServiceException("Retry interrupted", ie);
                        }
                    }
                }
            }
            metrics.recordFailedAttempt(); // Count final failure after all retries
            throw new EmailServiceException(
                "Failed after " + config.getMaxRetries() + " retries",
                lastException
            );
        }, executorService).orTimeout(config.getTimeoutMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Validates email address format.
     * @param email Email address to validate
     * @throws EmailServiceException if email format is invalid
     */
    private void validateEmailAddress(String email) {
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
        } catch (MessagingException e) {
            throw EmailServiceException.invalidEmailAddress(email);
        }
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
} 