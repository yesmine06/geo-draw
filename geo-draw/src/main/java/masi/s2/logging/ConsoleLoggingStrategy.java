package masi.s2.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleLoggingStrategy implements LoggingStrategy {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void log(String action, String details) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.printf("[%s] %s: %s%n", timestamp, action, details);
    }

    @Override
    public void logError(String error) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.err.printf("[%s] ERREUR: %s%n", timestamp, error);
    }

    @Override
    public void close() {
        // Rien à fermer pour la console
    }
} 