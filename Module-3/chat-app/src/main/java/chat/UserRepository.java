package chat;

import java.sql.*;

/**
 * UserRepository handles user-related database operations.
 */
public class UserRepository {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPass;

    public UserRepository(String dbUrl, String dbUser, String dbPass) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    public boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,32}$");
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 64;
    }

    public boolean registerUser(String username, String passwordHash) {
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

    public String getPasswordHash(String username) {
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
} 