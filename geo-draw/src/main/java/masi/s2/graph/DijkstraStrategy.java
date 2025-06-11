package masi.s2.graph;

import java.util.*;

public class DijkstraStrategy implements ShortestPathStrategy {
    @Override
    public List<Node> findShortestPath(Graph graph, Node start, Node end) throws IllegalArgumentException, PathNotFoundException {
        validateInput(graph, start, end);
        
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(
            Comparator.comparingDouble(distances::get)
        );

        // Initialisation
        for (Node node : graph.getNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            
            if (visited.contains(current)) continue;
            visited.add(current);

            if (current == end) {
                return reconstructPath(previousNodes, start, end);
            }

            for (Map.Entry<Node, Double> neighbor : current.getNeighbors().entrySet()) {
                Node next = neighbor.getKey();
                if (visited.contains(next)) continue;
                
                double weight = neighbor.getValue();
                double newDistance = distances.get(current) + weight;

                if (newDistance < distances.get(next)) {
                    distances.put(next, newDistance);
                    previousNodes.put(next, current);
                    queue.remove(next);
                    queue.add(next);
                }
            }
        }

        throw new PathNotFoundException("Aucun chemin trouvé entre " + start.getId() + " et " + end.getId());
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

    private List<Node> reconstructPath(Map<Node, Node> previousNodes, Node start, Node end) {
        List<Node> path = new ArrayList<>();
        Node current = end;
        while (current != null) {
            path.add(0, current);
            current = previousNodes.get(current);
        }
        return path;
    }

    @Override
    public boolean canHandle(Graph graph) {
        return graph != null && !graph.getNodes().isEmpty();
    }

    @Override
    public String getName() {
        return "Dijkstra";
    }

    @Override
    public String getDescription() {
        return "Algorithme de Dijkstra - Trouve le chemin le plus court dans un graphe à poids positifs";
    }
} 