package com.emailservice.exception;

/**
 * Custom exception class for email service related errors.
 * Provides detailed error messages and optional cause tracking.
 */
public class EmailServiceException extends RuntimeException {
    
    public EmailServiceException(String message) {
        super(message);
    }

    public EmailServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception for authentication failures.
     * @param username The username that failed authentication
     * @return EmailServiceException with appropriate error message
     */
    public static EmailServiceException authenticationError(String username) {
        return new EmailServiceException("Authentication failed for user: " + username);
    }

    /**
     * Creates an exception for invalid email address format.
     * @param email The invalid email address
     * @return EmailServiceException with appropriate error message
     */
    public static EmailServiceException invalidEmailAddress(String email) {
        return new EmailServiceException("Invalid email address: " + email);
    }
} 