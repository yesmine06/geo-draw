package masi.s2.logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseLogger implements Logger {
    private final String dbUrl;

    public DatabaseLogger(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    public void log(String message) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO logs (message) VALUES (?)")) {
            pstmt.setString(1, message);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 