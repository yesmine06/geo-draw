package masi.s2.logging;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseLoggingStrategy implements LoggingStrategy {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Connection connection;
    private PreparedStatement logStatement;
    private PreparedStatement errorStatement;

    public DatabaseLoggingStrategy(String dbPath) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        createTables();
        prepareStatements();
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp TEXT NOT NULL,
                    action TEXT NOT NULL,
                    details TEXT NOT NULL
                )
            """);
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS errors (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp TEXT NOT NULL,
                    error TEXT NOT NULL
                )
            """);
        }
    }

    private void prepareStatements() throws SQLException {
        logStatement = connection.prepareStatement(
            "INSERT INTO logs (timestamp, action, details) VALUES (?, ?, ?)"
        );
        errorStatement = connection.prepareStatement(
            "INSERT INTO errors (timestamp, error) VALUES (?, ?)"
        );
    }

    @Override
    public void log(String action, String details) {
        try {
            String timestamp = LocalDateTime.now().format(formatter);
            logStatement.setString(1, timestamp);
            logStatement.setString(2, action);
            logStatement.setString(3, details);
            logStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la journalisation : " + e.getMessage());
        }
    }

    @Override
    public void logError(String error) {
        try {
            String timestamp = LocalDateTime.now().format(formatter);
            errorStatement.setString(1, timestamp);
            errorStatement.setString(2, error);
            errorStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la journalisation d'erreur : " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            if (logStatement != null) logStatement.close();
            if (errorStatement != null) errorStatement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }

    public List<String> getAllLogs() throws SQLException {
        List<String> logs = new ArrayList<>();
        String query = "SELECT timestamp, action, details FROM logs ORDER BY timestamp DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String timestamp = rs.getString("timestamp");
                String action = rs.getString("action");
                String details = rs.getString("details");
                logs.add(String.format("[%s] %s: %s", timestamp, action, details));
            }
        }
        return logs;
    }
} 