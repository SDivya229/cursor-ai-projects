import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SmsService {
    // Provider types
    public enum Provider { TWILIO, NEXMO, MOCK }

    // Message status
    public enum Status { QUEUED, SENT, FAILED, RETRYING }

    // Message object
    public static class SmsMessage {
        public final String to;
        public final String body;
        public final Provider provider;
        public Status status;
        public int attempts;
        public String lastError;
        public final long createdAt;
        public SmsMessage(String to, String body, Provider provider) {
            this.to = to;
            this.body = body;
            this.provider = provider;
            this.status = Status.QUEUED;
            this.attempts = 0;
            this.lastError = null;
            this.createdAt = System.currentTimeMillis();
        }
    }

    // Configuration
    private final int maxRetries;
    private final int queueCapacity;
    private final long retryDelayMs;
    private final boolean autoFlush;
    private final int bulkLimit;
    private final Provider defaultProvider;

    // Internal state
    private final BlockingQueue<SmsMessage> queue;
    private final List<SmsMessage> history;
    private final AtomicInteger sentCount = new AtomicInteger(0);
    private final AtomicInteger failedCount = new AtomicInteger(0);
    private final AtomicInteger retryCount = new AtomicInteger(0);
    private final AtomicInteger queuedCount = new AtomicInteger(0);
    private final ExecutorService executor;
    private final SmsQueueProcessor processor;
    private final SmsStatistics stats;

    // Logger
    private void log(String msg) {
        System.out.println("[SmsService] " + msg);
    }

    // Constructor
    public SmsService(int maxRetries, int queueCapacity, long retryDelayMs, boolean autoFlush, int bulkLimit, Provider defaultProvider) {
        this.maxRetries = maxRetries;
        this.queueCapacity = queueCapacity;
        this.retryDelayMs = retryDelayMs;
        this.autoFlush = autoFlush;
        this.bulkLimit = bulkLimit;
        this.defaultProvider = defaultProvider;
        this.queue = new LinkedBlockingQueue<>(queueCapacity);
        this.history = Collections.synchronizedList(new ArrayList<>());
        this.stats = new SmsStatistics();
        this.processor = new SmsQueueProcessor(queue, history, stats, maxRetries, retryDelayMs);
        this.executor = Executors.newSingleThreadExecutor();
        this.executor.submit(processor);
    }

    // Stop service
    public void shutdown() {
        processor.stop();
        executor.shutdownNow();
        log("Service shutdown.");
    }

    // Validate phone number (simple)
    private boolean isValidNumber(String number) {
        return number != null && number.matches("\\+?[1-9][0-9]{7,14}");
    }

    // Validate message
    private boolean isValidMessage(String msg) {
        return msg != null && !msg.trim().isEmpty() && msg.length() <= 1600;
    }

    // Public send method (single)
    public boolean send(String to, String body) {
        return send(to, body, defaultProvider);
    }

    // Public send method (with provider)
    public boolean send(String to, String body, Provider provider) {
        if (!isValidNumber(to)) {
            log("Invalid phone number: " + to);
            return false;
        }
        if (!isValidMessage(body)) {
            log("Invalid message body.");
            return false;
        }
        SmsMessage msg = new SmsMessage(to, body, provider);
        boolean queued = queue.offer(msg);
        if (queued) {
            stats.incrementQueued();
            log("Message queued for " + to + " via " + provider);
            if (autoFlush) flush();
        } else {
            log("Queue full. Message not queued.");
            return false;
        }
        return true;
    }

    // Bulk send
    public int sendBulk(List<String> recipients, String body) {
        int count = 0;
        for (String to : recipients) {
            if (count >= bulkLimit) break;
            if (send(to, body)) count++;
        }
        log("Bulk send: " + count + " messages queued.");
        return count;
    }

    // Manual flush
    public void flush() {
        log("Manual flush triggered.");
        // No-op: queue processor is always running
    }

    // Get message history
    public List<SmsMessage> getHistory() {
        return new ArrayList<>(history);
    }

    // Get statistics
    public Map<String, Integer> getStats() {
        return stats.getStats();
    }

    // Print statistics
    public void printStats() {
        log("Stats: " + getStats());
    }

    // Example usage
    public static void main(String[] args) throws InterruptedException {
        SmsService service = new SmsService(3, 100, 500, false, 10, Provider.MOCK);
        service.send("+12345678901", "Hello, this is a test message!");
        service.send("+19876543210", "Another message", Provider.TWILIO);
        List<String> bulk = Arrays.asList("+11111111111", "+22222222222", "+33333333333");
        service.sendBulk(bulk, "Bulk message!");
        Thread.sleep(5000); // Wait for processing
        service.printStats();
        service.shutdown();
    }
} 