package chat;

import java.sql.*;

/**
 * DBUtil is now responsible only for initializing the database schema.
 */
public class DBUtil {
    private static final String DB_URL = System.getenv().getOrDefault("CHAT_DB_URL", "jdbc:h2:~/chatdb");
    private static final String DB_USER = System.getenv().getOrDefault("CHAT_DB_USER", "sa");
    private static final String DB_PASS = System.getenv().getOrDefault("CHAT_DB_PASS", "");

    static {
        initializeSchema();
    }

    public static void initializeSchema() {
        try {
            Class.forName("org.h2.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255), password VARCHAR(255))");
                stmt.execute("CREATE TABLE IF NOT EXISTS messages (id INT AUTO_INCREMENT PRIMARY KEY, sender VARCHAR(255), receiver VARCHAR(255), content VARCHAR(1000), timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDbUrl() { return DB_URL; }
    public static String getDbUser() { return DB_USER; }
    public static String getDbPass() { return DB_PASS; }
} 