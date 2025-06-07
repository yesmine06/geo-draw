package masi.s2.graph;

import java.util.*;

public class AStarStrategy implements ShortestPathStrategy {
    @Override
    public List<Node> findShortestPath(Graph graph, Node start, Node end) {
        Map<Node, Double> gScore = new HashMap<>();
        Map<Node, Double> fScore = new HashMap<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(
            Comparator.comparingDouble(fScore::get)
        );
        Set<Node> closedSet = new HashSet<>();

        // Initialisation
        for (Node node : graph.getNodes()) {
            gScore.put(node, Double.POSITIVE_INFINITY);
            fScore.put(node, Double.POSITIVE_INFINITY);
        }
        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, end));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current == end) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);

            for (Map.Entry<Node, Double> neighbor : current.getNeighbors().entrySet()) {
                Node next = neighbor.getKey();
                if (closedSet.contains(next)) continue;

                double tentativeGScore = gScore.get(current) + neighbor.getValue();

                if (tentativeGScore < gScore.get(next)) {
                    cameFrom.put(next, current);
                    gScore.put(next, tentativeGScore);
                    double hScore = heuristic(next, end);
                    fScore.put(next, tentativeGScore + hScore);

                    if (!openSet.contains(next)) {
                        openSet.add(next);
                    } else {
                        // Mise à jour de la priorité dans la file
                        openSet.remove(next);
                        openSet.add(next);
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    private double heuristic(Node a, Node b) {
        // Distance euclidienne comme heuristique admissible
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }
} 