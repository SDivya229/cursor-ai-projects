public class SmsMessage {
    public final String to;
    public final String body;
    public final SmsProvider provider;
    public SmsStatus status;
    public int attempts;
    public String lastError;
    public final long createdAt;

    public SmsMessage(String to, String body, SmsProvider provider) {
        this.to = to;
        this.body = body;
        this.provider = provider;
        this.status = SmsStatus.QUEUED;
        this.attempts = 0;
        this.lastError = null;
        this.createdAt = System.currentTimeMillis();
    }
} 