package chat;

/**
 * MessageDTO is a data transfer object for sending and saving messages.
 */
public class MessageDTO {
    private String sender;
    private String receiver;
    private String content;

    public MessageDTO(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getContent() { return content; }
} 