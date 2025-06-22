import java.util.Random;

public class MockProvider implements SmsProvider {
    private final Random rand = new Random();
    @Override
    public boolean send(SmsMessage message) {
        if (rand.nextInt(10) < 9) return true;
        message.lastError = "Mock: Simulated failure.";
        return false;
    }
    @Override
    public String getName() { return "MOCK"; }
} 