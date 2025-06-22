import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;
import java.util.Map;

public class SmsStatistics {
    private final AtomicInteger sent = new AtomicInteger();
    private final AtomicInteger failed = new AtomicInteger();
    private final AtomicInteger retried = new AtomicInteger();
    private final AtomicInteger queued = new AtomicInteger();

    public void incrementSent() { sent.incrementAndGet(); }
    public void incrementFailed() { failed.incrementAndGet(); }
    public void incrementRetried() { retried.incrementAndGet(); }
    public void incrementQueued() { queued.incrementAndGet(); }
    public void decrementQueued() { queued.decrementAndGet(); }

    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("sent", sent.get());
        stats.put("failed", failed.get());
        stats.put("retried", retried.get());
        stats.put("queued", queued.get());
        return stats;
    }
} 