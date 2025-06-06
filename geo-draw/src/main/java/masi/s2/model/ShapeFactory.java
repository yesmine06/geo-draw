package masi.s2.model;

public class ShapeFactory {
    public static Shape createShape(String type) {
        switch (type.toLowerCase()) {
            case "rectangle":
                return new RectangleShape();
            case "circle":
                return new CircleShape();
            case "line":
                return new LineShape();
            default:
                throw new IllegalArgumentException("Type de forme non supporté: " + type);
        }
    }
} 