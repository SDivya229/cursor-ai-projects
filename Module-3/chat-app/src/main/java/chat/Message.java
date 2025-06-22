package chat;

import java.sql.Timestamp;

/**
 * Message represents a single chat message exchanged between users.
 * <p>
 * <b>Purpose:</b> Encapsulates sender, content, and timestamp.
 * <br><b>Usage:</b>
 * <pre>
 *   Message m = new Message("alice", "Hello!", new Timestamp(System.currentTimeMillis()));
 * </pre>
 */
public class Message {
    private String sender;
    private String content;
    private Timestamp timestamp;

    /**
     * Constructs a new Message.
     * @param sender Username of the sender
     * @param content Message content
     * @param timestamp Time the message was sent
     */
    public Message(String sender, String content, Timestamp timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    /**
     * Gets the sender's username.
     * @return Sender username
     */
    public String getSender() { return sender; }

    /**
     * Gets the message content.
     * @return Message content
     */
    public String getContent() { return content; }

    /**
     * Gets the timestamp of the message.
     * @return Timestamp
     */
    public Timestamp getTimestamp() { return timestamp; }
} 