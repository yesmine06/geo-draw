package masi.s2.adapter;

import javafx.scene.canvas.GraphicsContext;

public class RectangleAdapter implements ShapeAdapter {
    @Override
    public void draw(GraphicsContext gc, double startX, double startY, double endX, double endY, boolean isFilled) {
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        
        if (isFilled) {
            gc.fillRect(x, y, width, height);
        }
        gc.strokeRect(x, y, width, height);
    }

    @Override
    public String getShapeName() {
        return "Rectangle";
    }
} 