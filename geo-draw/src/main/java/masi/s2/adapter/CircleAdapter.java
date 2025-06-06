package masi.s2.adapter;

import javafx.scene.canvas.GraphicsContext;

public class CircleAdapter implements ShapeAdapter {
    @Override
    public void draw(GraphicsContext gc, double startX, double startY, double endX, double endY, boolean isFilled) {
        double radius = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        if (isFilled) {
            gc.fillOval(startX - radius, startY - radius, radius * 2, radius * 2);
        }
        gc.strokeOval(startX - radius, startY - radius, radius * 2, radius * 2);
    }

    @Override
    public String getShapeName() {
        return "Cercle";
    }
} 