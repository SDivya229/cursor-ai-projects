package com.emailservice.message;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Builder class for constructing email messages.
 * Provides a fluent interface for setting message properties.
 */
public class EmailMessageBuilder {
    private final Message message;

    /**
     * Creates a new message builder with the given session.
     * @param session Mail session to create message with
     */
    public EmailMessageBuilder(Session session) {
        this.message = new MimeMessage(session);
    }

    /**
     * Sets the sender address.
     * @param from Sender's email address
     * @return this builder for method chaining
     * @throws MessagingException if the address is invalid
     */
    public EmailMessageBuilder from(String from) throws MessagingException {
        message.setFrom(new InternetAddress(from));
        return this;
    }

    /**
     * Sets the recipient address.
     * @param to Recipient's email address
     * @return this builder for method chaining
     * @throws MessagingException if the address is invalid
     */
    public EmailMessageBuilder to(String to) throws MessagingException {
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        return this;
    }

    /**
     * Sets the email subject.
     * @param subject Email subject
     * @return this builder for method chaining
     * @throws MessagingException if setting the subject fails
     */
    public EmailMessageBuilder subject(String subject) throws MessagingException {
        message.setSubject(subject);
        return this;
    }

    /**
     * Sets the email body content.
     * @param body Email body text
     * @return this builder for method chaining
     * @throws MessagingException if setting the content fails
     */
    public EmailMessageBuilder body(String body) throws MessagingException {
        message.setText(body);
        return this;
    }

    /**
     * Builds and returns the constructed message.
     * @return The constructed Message instance
     */
    public Message build() {
        return message;
    }
} 