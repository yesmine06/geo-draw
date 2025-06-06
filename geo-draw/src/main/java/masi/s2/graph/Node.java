package masi.s2.graph;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class Node {
    public static final double NODE_RADIUS = 20;
    private double x;
    private double y;
    private String id;
    private Map<Node, Double> neighbors;
    private boolean isSelected;
    private boolean isStart;
    private boolean isEnd;
    private boolean isInPath;

    public Node(double x, double y, String id) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.neighbors = new HashMap<>();
        this.isSelected = false;
        this.isStart = false;
        this.isEnd = false;
        this.isInPath = false;
    }

    public void addNeighbor(Node neighbor, double weight) {
        neighbors.put(neighbor, weight);
    }

    public void draw(GraphicsContext gc) {
        // Déterminer la couleur en fonction de l'état du nœud
        if (isStart) {
            gc.setFill(Color.GREEN);
        } else if (isEnd) {
            gc.setFill(Color.RED);
        } else if (isInPath) {
            gc.setFill(Color.ORANGE);
        } else if (isSelected) {
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(Color.BLUE);
        }
        
        // Dessiner le cercle du nœud
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Dessiner le contour
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Dessiner l'ID du nœud
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 12));
        
        // Centrer le texte
        double textWidth = gc.getFont().getSize() * id.length() * 0.6;
        double textHeight = gc.getFont().getSize();
        gc.fillText(id, x - textWidth / 2, y + textHeight / 4);
    }

    public void drawEdges(GraphicsContext gc) {
        for (Map.Entry<Node, Double> entry : neighbors.entrySet()) {
            Node neighbor = entry.getKey();
            Double weight = entry.getValue();
            
            // Dessiner la ligne
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeLine(x, y, neighbor.getX(), neighbor.getY());
            
            // Dessiner le poids
            gc.setFill(Color.BLACK);
            double midX = (x + neighbor.getX()) / 2;
            double midY = (y + neighbor.getY()) / 2;
            gc.fillText(String.format("%.1f", weight), midX, midY);
        }
    }

    // Getters et setters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getId() {
        return id;
    }

    public Map<Node, Double> getNeighbors() {
        return neighbors;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setStart(boolean start) {
        this.isStart = start;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setEnd(boolean end) {
        this.isEnd = end;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setInPath(boolean inPath) {
        this.isInPath = inPath;
    }

    public boolean isInPath() {
        return isInPath;
    }
} 
