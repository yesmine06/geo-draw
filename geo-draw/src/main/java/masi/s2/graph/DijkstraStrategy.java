package masi.s2.graph;

import java.util.*;

public class DijkstraStrategy implements ShortestPathStrategy {
    @Override
    public List<Node> findShortestPath(Graph graph, Node start, Node end) {
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
            
            // Si on a déjà visité ce nœud, on passe au suivant
            if (visited.contains(current)) continue;
            visited.add(current);

            if (current == end) {
                break;
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

        // Reconstruction du chemin
        List<Node> path = new ArrayList<>();
        Node current = end;
        while (current != null) {
            path.add(0, current);
            current = previousNodes.get(current);
        }

        return path.isEmpty() || path.get(0) != start ? new ArrayList<>() : path;
    }
} 