package masi.s2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ActionLogger {
    private static final String LOG_FILE = "drawing_actions.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private List<DrawingAction> actions;
    private final ExecutorService executor;
    private final AtomicInteger pendingWrites;
    private static final int WRITE_THRESHOLD = 10; // Nombre d'actions avant écriture

    public ActionLogger() {
        this.executor = Executors.newSingleThreadExecutor();
        this.pendingWrites = new AtomicInteger(0);
        loadActions();
    }

    private void loadActions() {
        File file = new File(LOG_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                actions = gson.fromJson(reader, new TypeToken<List<DrawingAction>>(){}.getType());
            } catch (IOException e) {
                System.err.println("Erreur lors de la lecture du fichier de log : " + e.getMessage());
                actions = new ArrayList<>();
            }
        } else {
            actions = new ArrayList<>();
        }
    }

    public void logAction(String shapeType, double startX, double startY, double endX, double endY, String color) {
        DrawingAction action = new DrawingAction(
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            shapeType,
            startX,
            startY,
            endX,
            endY,
            color
        );
        
        synchronized (actions) {
            actions.add(action);
            int pending = pendingWrites.incrementAndGet();
            
            if (pending >= WRITE_THRESHOLD) {
                scheduleWrite();
            }
        }
    }

    private void scheduleWrite() {
        executor.submit(() -> {
            List<DrawingAction> actionsToWrite;
            synchronized (actions) {
                actionsToWrite = new ArrayList<>(actions);
                pendingWrites.set(0);
            }
            
            try (Writer writer = new FileWriter(LOG_FILE)) {
                gson.toJson(actionsToWrite, writer);
            } catch (IOException e) {
                System.err.println("Erreur lors de l'écriture dans le fichier de log : " + e.getMessage());
            }
        });
    }

    public void shutdown() {
        executor.submit(() -> {
            synchronized (actions) {
                try (Writer writer = new FileWriter(LOG_FILE)) {
                    gson.toJson(actions, writer);
                } catch (IOException e) {
                    System.err.println("Erreur lors de l'écriture finale dans le fichier de log : " + e.getMessage());
                }
            }
        });
        executor.shutdown();
    }

    private static class DrawingAction {
        private final String timestamp;
        private final String shapeType;
        private final double startX;
        private final double startY;
        private final double endX;
        private final double endY;
        private final String color;

        public DrawingAction(String timestamp, String shapeType, double startX, double startY, 
                           double endX, double endY, String color) {
            this.timestamp = timestamp;
            this.shapeType = shapeType;
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.color = color;
        }
    }
} 