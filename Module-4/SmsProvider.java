public interface SmsProvider {
    boolean send(SmsMessage message);
    String getName();
} 