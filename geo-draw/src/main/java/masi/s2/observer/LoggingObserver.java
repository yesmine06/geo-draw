package masi.s2.observer;

import masi.s2.logging.LoggingManager;

public class LoggingObserver implements CanvasObserver {
    private final LoggingManager loggingManager;

    public LoggingObserver(LoggingManager loggingManager) {
        this.loggingManager = loggingManager;
    }

    @Override
    public void onCanvasEvent(CanvasEvent event) {
        switch (event.getType()) {
            case DRAW:
                loggingManager.log("Dessin", "Forme dessinée");
                break;
            case CLEAR:
                loggingManager.log("Effacement", "Canvas effacé");
                break;
            case THEME_CHANGED:
                boolean isDarkTheme = (boolean) event.getData();
                loggingManager.log("Thème", "Thème " + (isDarkTheme ? "sombre" : "clair") + " activé");
                break;
            case SHAPE_SELECTED:
                loggingManager.log("Sélection", "Forme sélectionnée : " + event.getData());
                break;
            case COLOR_CHANGED:
                loggingManager.log("Couleur", "Couleur changée");
                break;
            case STROKE_WIDTH_CHANGED:
                loggingManager.log("Style", "Épaisseur du trait modifiée");
                break;
            case ROTATION_CHANGED:
                loggingManager.log("Style", "Rotation modifiée");
                break;
            case FILL_CHANGED:
                boolean isFilled = (boolean) event.getData();
                loggingManager.log("Style", "Remplissage " + (isFilled ? "activé" : "désactivé"));
                break;
        }
    }
} 