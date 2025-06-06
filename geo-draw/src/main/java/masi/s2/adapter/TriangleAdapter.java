package masi.s2.adapter;

import javafx.scene.canvas.GraphicsContext;

public class TriangleAdapter implements ShapeAdapter {
    @Override
    public void draw(GraphicsContext gc, double startX, double startY, double endX, double endY, boolean isFilled) {
        double size = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
        double cx = (startX + endX) / 2;
        double cy = (startY + endY) / 2;
        double h = size * Math.sqrt(3) / 2;
        double[] xPoints = {cx, cx - size / 2, cx + size / 2};
        double[] yPoints = {cy - h / 2, cy + h / 2, cy + h / 2};
        if (isFilled) {
            gc.fillPolygon(xPoints, yPoints, 3);
        }
        gc.strokePolygon(xPoints, yPoints, 3);
    }

    @Override
    public String getShapeName() {
        return "Triangle";
    }
} 