package masi.s2.logging;

public class LoggingManager {
    private static LoggingManager instance;
    private LoggingStrategy strategy;

    // Constructeur privé pour empêcher l'instanciation directe
    private LoggingManager(LoggingStrategy strategy) {
        this.strategy = strategy;
    }

    // Méthode pour obtenir l'instance unique
    public static synchronized LoggingManager getInstance(LoggingStrategy strategy) {
        if (instance == null) {
            instance = new LoggingManager(strategy);
        }
        return instance;
    }

    // Méthode pour obtenir l'instance existante
    public static synchronized LoggingManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("LoggingManager n'a pas été initialisé. Utilisez getInstance(LoggingStrategy) d'abord.");
        }
        return instance;
    }

    public void setStrategy(LoggingStrategy strategy) {
        if (this.strategy != null) {
            this.strategy.close();
        }
        this.strategy = strategy;
    }

    public void log(String action, String details) {
        if (strategy != null) {
            strategy.log(action, details);
        }
    }

    public void logError(String error) {
        if (strategy != null) {
            strategy.logError(error);
        }
    }

    public void close() {
        if (strategy != null) {
            strategy.close();
        }
    }

    public LoggingStrategy getStrategy() {
        return strategy;
    }
} 