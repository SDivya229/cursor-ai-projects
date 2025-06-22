import java.util.concurrent.BlockingQueue;
import java.util.List;

public class SmsQueueProcessor implements Runnable {
    private final BlockingQueue<SmsMessage> queue;
    private final List<SmsMessage> history;
    private final SmsStatistics stats;
    private final int maxRetries;
    private final long retryDelayMs;
    private volatile boolean running = true;

    public SmsQueueProcessor(BlockingQueue<SmsMessage> queue, List<SmsMessage> history, SmsStatistics stats, int maxRetries, long retryDelayMs) {
        this.queue = queue;
        this.history = history;
        this.stats = stats;
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
    }

    public void stop() { running = false; }

    @Override
    public void run() {
        while (running) {
            try {
                SmsMessage msg = queue.poll(500, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (msg != null) processMessage(msg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processMessage(SmsMessage msg) {
        boolean sent = false;
        while (msg.attempts <= maxRetries && !sent) {
            msg.attempts++;
            msg.status = SmsStatus.RETRYING;
            stats.incrementRetried();
            try {
                Thread.sleep((long) (retryDelayMs * Math.pow(2, msg.attempts - 1)));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            sent = msg.provider.send(msg);
            if (sent) {
                msg.status = SmsStatus.SENT;
                stats.incrementSent();
            } else {
                msg.status = SmsStatus.FAILED;
                stats.incrementFailed();
            }
        }
        history.add(msg);
        stats.decrementQueued();
    }
} 