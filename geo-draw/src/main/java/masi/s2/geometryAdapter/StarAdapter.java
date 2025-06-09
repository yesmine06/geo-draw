package masi.s2.geometryAdapter;

import javafx.scene.canvas.GraphicsContext;

public class StarAdapter implements ShapeAdapter {
    @Override
    public void draw(GraphicsContext gc, double startX, double startY, double endX, double endY, boolean isFilled) {
        double size = Math.max(Math.abs(endX - startX), Math.abs(endY - startY)) / 2;
        double cx = (startX + endX) / 2;
        double cy = (startY + endY) / 2;
        double[] xPoints = new double[10];
        double[] yPoints = new double[10];
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2 + i * Math.PI / 5;
            double r = (i % 2 == 0) ? size : size * 0.4;
            xPoints[i] = cx + r * Math.cos(angle);
            yPoints[i] = cy - r * Math.sin(angle);
        }
        if (isFilled) {
            gc.fillPolygon(xPoints, yPoints, 10);
        }
        gc.strokePolygon(xPoints, yPoints, 10);
    }

    @Override
    public String getShapeName() {
        return "Étoile";
    }
} 