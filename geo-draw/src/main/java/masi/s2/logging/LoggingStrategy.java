package masi.s2.logging;

import java.time.LocalDateTime;

public interface LoggingStrategy {
    void log(String action, String details);
    void logError(String error);
    void close();
} 