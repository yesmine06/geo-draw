package masi.s2.adapter;

public class ShapeAdapterFactory {
    public static ShapeAdapter createAdapter(String shapeName) {
        switch (shapeName) {
            case "Rectangle":
                return new RectangleAdapter();
            case "Cercle":
                return new CircleAdapter();
            case "Ligne":
                return new LineAdapter();
            case "Triangle":
                return new TriangleAdapter();
            case "Étoile":
            case "Etoile":
                return new StarAdapter();
            default:
                return null;
        }
    }
} 