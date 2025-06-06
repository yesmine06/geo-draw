package masi.s2.logging;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLoggingStrategy implements LoggingStrategy {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final PrintWriter writer;
    private final String logFile;
    
    /**
     * Returns the path to the log file.
     * @return the log file path
     */
    public String getLogFile() {
        return logFile;
    }

    public FileLoggingStrategy(String logFile) throws IOException {
        this.logFile = logFile;
        this.writer = new PrintWriter(new FileWriter(logFile, true));
    }

    @Override
    public void log(String action, String details) {
        String timestamp = LocalDateTime.now().format(formatter);
        writer.printf("[%s] %s: %s%n", timestamp, action, details);
        writer.flush();
    }

    @Override
    public void logError(String error) {
        String timestamp = LocalDateTime.now().format(formatter);
        writer.printf("[%s] ERREUR: %s%n", timestamp, error);
        writer.flush();
    }

    @Override
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
} 
