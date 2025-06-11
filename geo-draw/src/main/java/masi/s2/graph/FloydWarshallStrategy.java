package masi.s2.graph;

import java.util.*;

public class FloydWarshallStrategy implements ShortestPathStrategy {
    @Override
    public List<Node> findShortestPath(Graph graph, Node start, Node end) throws IllegalArgumentException, PathNotFoundException {
        validateInput(graph, start, end);
        
        Map<Node, Map<Node, Double>> distances = new HashMap<>();
        Map<Node, Map<Node, Node>> next = new HashMap<>();
        
        // Initialisation
        for (Node i : graph.getNodes()) {
            distances.put(i, new HashMap<>());
            next.put(i, new HashMap<>());
            for (Node j : graph.getNodes()) {
                distances.get(i).put(j, Double.POSITIVE_INFINITY);
                next.get(i).put(j, null);
            }
            distances.get(i).put(i, 0.0);
        }

        // Remplir les distances initiales
        for (Node i : graph.getNodes()) {
            for (Map.Entry<Node, Double> neighbor : i.getNeighbors().entrySet()) {
                Node j = neighbor.getKey();
                double weight = neighbor.getValue();
                distances.get(i).put(j, weight);
                next.get(i).put(j, j);
            }
        }

        // Algorithme Floyd-Warshall
        for (Node k : graph.getNodes()) {
            for (Node i : graph.getNodes()) {
                for (Node j : graph.getNodes()) {
                    double ik = distances.get(i).get(k);
                    double kj = distances.get(k).get(j);
                    double ij = distances.get(i).get(j);
                    
                    if (ik != Double.POSITIVE_INFINITY && 
                        kj != Double.POSITIVE_INFINITY && 
                        ik + kj < ij) {
                        distances.get(i).put(j, ik + kj);
                        next.get(i).put(j, next.get(i).get(k));
                    }
                }
            }
        }

        // Vérifier si un chemin existe
        if (distances.get(start).get(end) == Double.POSITIVE_INFINITY) {
            throw new PathNotFoundException("Aucun chemin trouvé entre " + start.getId() + " et " + end.getId());
        }

        // Reconstruire le chemin
        return reconstructPath(next, start, end);
    }

    private void validateInput(Graph graph, Node start, Node end) throws IllegalArgumentException {
        if (graph == null) {
            throw new IllegalArgumentException("Le graphe ne peut pas être null");
        }
        if (start == null) {
            throw new IllegalArgumentException("Le nœud de départ ne peut pas être null");
        }
        if (end == null) {
            throw new IllegalArgumentException("Le nœud d'arrivée ne peut pas être null");
        }
        if (!graph.getNodes().contains(start)) {
            throw new IllegalArgumentException("Le nœud de départ n'appartient pas au graphe");
        }
        if (!graph.getNodes().contains(end)) {
            throw new IllegalArgumentException("Le nœud d'arrivée n'appartient pas au graphe");
        }
    }

    private List<Node> reconstructPath(Map<Node, Map<Node, Node>> next, Node start, Node end) {
        List<Node> path = new ArrayList<>();
        if (next.get(start).get(end) == null) {
            return path;
        }

        path.add(start);
        while (start != end) {
            start = next.get(start).get(end);
            path.add(start);
        }
        return path;
    }

    @Override
    public boolean canHandle(Graph graph) {
        return graph != null && !graph.getNodes().isEmpty();
    }

    @Override
    public String getName() {
        return "Floyd-Warshall";
    }

    @Override
    public String getDescription() {
        return "Algorithme Floyd-Warshall - Trouve les chemins les plus courts entre toutes les paires de nœuds";
    }
} 