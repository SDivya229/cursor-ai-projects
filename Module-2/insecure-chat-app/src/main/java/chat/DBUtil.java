package chat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtil {
    private static final String DB_URL = System.getenv().getOrDefault("CHAT_DB_URL", "jdbc:h2:~/insecurechatdb");
    private static final String DB_USER = System.getenv().getOrDefault("CHAT_DB_USER", "sa");
    private static final String DB_PASS = System.getenv().getOrDefault("CHAT_DB_PASS", "");

    static {
        try {
            Class.forName("org.h2.Driver");
            try (Connection conn = getConnection()) {
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255), password VARCHAR(255))");
                stmt.execute("CREATE TABLE IF NOT EXISTS messages (id INT AUTO_INCREMENT PRIMARY KEY, sender VARCHAR(255), receiver VARCHAR(255), content VARCHAR(1000), timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,32}$");
    }
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 64;
    }
    public static String escapeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }

    public static boolean registerUser(String username, String passwordHash) {
        if (!isValidUsername(username) || passwordHash == null) return false;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getPasswordHash(String username) {
        if (!isValidUsername(username)) return null;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE username = ?");) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            // log error
        }
        return null;
    }

    public static void saveMessage(String sender, String receiver, String content) {
        if (!isValidUsername(sender) || !isValidUsername(receiver) || content == null) return;
        String safeContent = escapeHtml(content);
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO messages (sender, receiver, content) VALUES (?, ?, ?)");) {
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, safeContent);
            ps.executeUpdate();
        } catch (SQLException e) {
            // log error
        }
    }

    public static List<Message> getMessages(String user1, String user2, int limit, int offset) {
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
} 