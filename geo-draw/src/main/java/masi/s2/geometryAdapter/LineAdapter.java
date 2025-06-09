package masi.s2.geometryAdapter;

import javafx.scene.canvas.GraphicsContext;

public class LineAdapter implements ShapeAdapter {
    @Override
    public void draw(GraphicsContext gc, double startX, double startY, double endX, double endY, boolean isFilled) {
        gc.strokeLine(startX, startY, endX, endY);
    }

    @Override
    public String getShapeName() {
        return "Ligne";
    }
} 