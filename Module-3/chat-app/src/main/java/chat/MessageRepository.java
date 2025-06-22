package chat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MessageRepository handles message-related database operations.
 */
public class MessageRepository {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPass;

    public MessageRepository(String dbUrl, String dbUser, String dbPass) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    public void saveMessage(MessageDTO dto) {
        if (!isValidUsername(dto.getSender()) || !isValidUsername(dto.getReceiver()) || dto.getContent() == null) return;
        String safeContent = escapeHtml(dto.getContent());
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO messages (sender, receiver, content) VALUES (?, ?, ?)");) {
            ps.setString(1, dto.getSender());
            ps.setString(2, dto.getReceiver());
            ps.setString(3, safeContent);
            ps.executeUpdate();
        } catch (SQLException e) {
            // log error
        }
    }

    public List<Message> getMessages(String user1, String user2, int limit, int offset) {
        List<Message> messages = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM messages WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) ORDER BY timestamp LIMIT ? OFFSET ?")) {
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);
            ps.setInt(5, limit);
            ps.setInt(6, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(new Message(
                        escapeHtml(rs.getString("sender")),
                        escapeHtml(rs.getString("content")),
                        rs.getTimestamp("timestamp")
                    ));
                }
            }
        } catch (SQLException e) {
            // log error
        }
        return messages;
    }

    public boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,32}$");
    }

    public String escapeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
} 