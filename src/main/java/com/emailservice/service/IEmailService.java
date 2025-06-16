package com.emailservice.service;

import java.util.concurrent.CompletableFuture;

/**
 * Interface defining the contract for email service operations.
 * Implementations should handle email sending operations asynchronously.
 */
public interface IEmailService extends AutoCloseable {
    /**
     * Sends an email asynchronously.
     * @param from Sender's email address
     * @param to Recipient's email address
     * @param subject Email subject
     * @param body Email body content
     * @return CompletableFuture<String> containing success message
     */
    CompletableFuture<String> sendEmailAsync(String from, String to, String subject, String body);

    /**
     * Gets the total number of emails sent successfully.
     * @return CompletableFuture<Long> containing the count of sent emails
     */
    CompletableFuture<Long> getEmailsSentCount();

    /**
     * Gets the total number of failed email attempts.
     * @return CompletableFuture<Long> containing the count of failed attempts
     */
    CompletableFuture<Long> getFailedAttemptsCount();
} 