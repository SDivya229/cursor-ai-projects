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

/**
 * WebSocketServerImpl provides real-time chat functionality using WebSockets.
 * <p>
 * <b>Purpose:</b> Enables instant message delivery to all connected clients.
 * <br><b>Key Features:</b>
 * <ul>
 *   <li>Handles WebSocket connections, messages, and errors.</li>
 *   <li>Broadcasts messages to all clients.</li>
 *   <li>Implements basic rate limiting per connection.</li>
 *   <li>Performs basic authentication via handshake token.</li>
 * </ul>
 * <b>Usage:</b>
 * <pre>
 *   WebSocketServerImpl server = new WebSocketServerImpl(8080);
 *   server.start();
 * </pre>
 * <b>Dependencies:</b> Requires org.java_websocket library.
 */
public class WebSocketServerImpl extends WebSocketServer {
    private static Set<WebSocket> conns = Collections.synchronizedSet(new HashSet<>());
    private static Map<WebSocket, Long> lastMessageTime = new ConcurrentHashMap<>();
    private static final long MESSAGE_INTERVAL_MS = 500; // basic rate limit

    /**
     * Constructs a WebSocketServerImpl on the specified port.
     * @param port TCP port to listen on
     */
    public WebSocketServerImpl(int port) {
        super(new InetSocketAddress(port));
    }

    /**
     * Called when a new WebSocket connection is opened.
     * @param conn WebSocket connection
     * @param handshake Client handshake data
     */
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

    /**
     * Called when a WebSocket connection is closed.
     * @param conn WebSocket connection
     * @param code Close code
     * @param reason Reason for closing
     * @param remote Whether closed by remote peer
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conns.remove(conn);
        lastMessageTime.remove(conn);
    }

    /**
     * Called when a message is received from a client.
     * @param conn WebSocket connection
     * @param message Message content
     * <p><b>Errors:</b> Sends error message if rate limit exceeded.</p>
     */
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

    /**
     * Called when an error occurs on a connection.
     * @param conn WebSocket connection (may be null)
     * @param ex Exception thrown
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
        // Log error securely
        System.err.println("WebSocket error: " + ex.getMessage());
    }

    /**
     * Called when the server starts successfully.
     */
    @Override
    public void onStart() {
        System.out.println("WebSocket server started on port " + getPort());
    }
} 