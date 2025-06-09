package masi.s2.geometryAdapter;

import javafx.scene.canvas.GraphicsContext;

public interface ShapeAdapter {
    void draw(GraphicsContext gc, double startX, double startY, double endX, double endY, boolean isFilled);
    String getShapeName();
} 