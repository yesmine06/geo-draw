package masi.s2.geometryAdapter;

import java.util.HashMap;
import java.util.Map;

public class ShapeAdapterFactory {
    private static final Map<String, ShapeAdapter> adapters = new HashMap<>();
    
    static {
        // Enregistrement des adaptateurs par défaut
        registerAdapter("Rectangle", new RectangleAdapter());
        registerAdapter("Cercle", new CircleAdapter());
        registerAdapter("Ligne", new LineAdapter());
        registerAdapter("Triangle", new TriangleAdapter());
        registerAdapter("Étoile", new StarAdapter());
        registerAdapter("Etoile", new StarAdapter());
    }
    
    public static void registerAdapter(String shapeName, ShapeAdapter adapter) {
        adapters.put(shapeName, adapter);
    }
    
    public static ShapeAdapter createAdapter(String shapeName) {
        return adapters.get(shapeName);
    }
} 