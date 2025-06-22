package chat;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketServerImpl extends WebSocketServer {
    private static Set<WebSocket> conns = Collections.synchronizedSet(new HashSet<>());
    private static Map<WebSocket, Long> lastMessageTime = new ConcurrentHashMap<>();
    private static final long MESSAGE_INTERVAL_MS = 500; // basic rate limit

    public WebSocketServerImpl(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String token = handshake.getFieldValue("Sec-WebSocket-Protocol");
        // In a real app, validate the token (e.g., session or JWT)
        if (token == null || token.isEmpty()) {
            conn.close(1008, "Authentication required");
            return;
        }
        conns.add(conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conns.remove(conn);
        lastMessageTime.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        long now = System.currentTimeMillis();
        Long last = lastMessageTime.getOrDefault(conn, 0L);
        if (now - last < MESSAGE_INTERVAL_MS) {
            conn.send("{\"error\":\"Rate limit exceeded\"}");
            return;
        }
        lastMessageTime.put(conn, now);
        String safeMessage = chat.DBUtil.escapeHtml(message);
        for (WebSocket sock : conns) {
            sock.send(safeMessage);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // Log error securely
        System.err.println("WebSocket error: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started on port " + getPort());
    }
} 