package masi.s2.graph;

import java.util.*;

public class AStarStrategy implements ShortestPathStrategy {
    @Override
    public List<Node> findShortestPath(Graph graph, Node start, Node end) throws IllegalArgumentException, PathNotFoundException {
        validateInput(graph, start, end);
        
        Map<Node, Double> gScore = new HashMap<>();
        Map<Node, Double> fScore = new HashMap<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        Set<Node> openSet = new HashSet<>();
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
            Node current = getLowestFScore(openSet, fScore);
            
            if (current == end) {
                return reconstructPath(cameFrom, current);
            }

            openSet.remove(current);
            closedSet.add(current);

            for (Map.Entry<Node, Double> neighbor : current.getNeighbors().entrySet()) {
                Node next = neighbor.getKey();
                if (closedSet.contains(next)) continue;

                double tentativeGScore = gScore.get(current) + neighbor.getValue();

                if (!openSet.contains(next)) {
                    openSet.add(next);
                } else if (tentativeGScore >= gScore.get(next)) {
                    continue;
                }

                cameFrom.put(next, current);
                gScore.put(next, tentativeGScore);
                fScore.put(next, gScore.get(next) + heuristic(next, end));
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

    private double heuristic(Node current, Node end) {
        // Distance euclidienne entre les nœuds
        double dx = current.getX() - end.getX();
        double dy = current.getY() - end.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private Node getLowestFScore(Set<Node> openSet, Map<Node, Double> fScore) {
        return openSet.stream()
            .min(Comparator.comparingDouble(fScore::get))
            .orElseThrow(() -> new IllegalStateException("L'ensemble ouvert est vide"));
    }

    private List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(0, current);
            current = cameFrom.get(current);
        }
        return path;
    }

    @Override
    public boolean canHandle(Graph graph) {
        return graph != null && !graph.getNodes().isEmpty();
    }

    @Override
    public String getName() {
        return "A*";
    }

    @Override
    public String getDescription() {
        return "Algorithme A* - Trouve le chemin le plus court en utilisant une heuristique basée sur la distance euclidienne";
    }
} 