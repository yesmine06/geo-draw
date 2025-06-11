package masi.s2.observer;

import javafx.scene.control.Label;

public class StatusBarObserver implements CanvasObserver {
    private final Label statusLabel;
    private boolean isDarkTheme;

    public StatusBarObserver(Label statusLabel, boolean isDarkTheme) {
        this.statusLabel = statusLabel;
        this.isDarkTheme = isDarkTheme;
    }

    @Override
    public void onCanvasEvent(CanvasEvent event) {
        switch (event.getType()) {
            case DRAW:
                updateStatus("Forme dessinée");
                break;
            case CLEAR:
                updateStatus("Canvas effacé");
                break;
            case THEME_CHANGED:
                isDarkTheme = (boolean) event.getData();
                updateStatus("Thème " + (isDarkTheme ? "sombre" : "clair") + " activé");
                break;
            case SHAPE_SELECTED:
                updateStatus("Forme sélectionnée : " + event.getData());
                break;
            case COLOR_CHANGED:
                updateStatus("Couleur changée");
                break;
            case STROKE_WIDTH_CHANGED:
                updateStatus("Épaisseur du trait modifiée");
                break;
            case ROTATION_CHANGED:
                updateStatus("Rotation modifiée");
                break;
            case FILL_CHANGED:
                updateStatus("Remplissage " + ((boolean) event.getData() ? "activé" : "désactivé"));
                break;
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
} 