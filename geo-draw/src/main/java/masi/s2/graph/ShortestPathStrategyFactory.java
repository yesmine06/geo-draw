package masi.s2.graph;

import java.util.HashMap;
import java.util.Map;

public class ShortestPathStrategyFactory {
    private static final Map<String, ShortestPathStrategy> strategies = new HashMap<>();
    
    static {
        registerStrategy(new DijkstraStrategy());
        registerStrategy(new AStarStrategy());
        registerStrategy(new FloydWarshallStrategy());
    }
    
    public static void registerStrategy(ShortestPathStrategy strategy) {
        strategies.put(strategy.getName(), strategy);
    }
    
    public static ShortestPathStrategy createStrategy(String name) {
        ShortestPathStrategy strategy = strategies.get(name);
        if (strategy == null) {
            throw new IllegalArgumentException("Stratégie non trouvée : " + name);
        }
        return strategy;
    }
    
    public static ShortestPathStrategy getBestStrategy(Graph graph) {
        return strategies.values().stream()
            .filter(strategy -> strategy.canHandle(graph))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Aucune stratégie appropriée trouvée pour ce graphe"));
    }
    
    public static Map<String, String> getAvailableStrategies() {
        Map<String, String> result = new HashMap<>();
        for (ShortestPathStrategy strategy : strategies.values()) {
            result.put(strategy.getName(), strategy.getDescription());
        }
        return result;
    }
} 