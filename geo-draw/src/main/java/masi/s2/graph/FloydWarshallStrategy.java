package masi.s2.graph;

import java.util.*;

public class FloydWarshallStrategy implements ShortestPathStrategy {

    @Override
    public List<Node> findShortestPath(Graph graph, Node start, Node end) {
        List<Node> nodes = graph.getNodes();
        if (start == null || end == null || nodes.isEmpty()) {
            return new ArrayList<>();
        }

        // Map nodes to indices for matrix representation
        Map<Node, Integer> nodeToIndex = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeToIndex.put(nodes.get(i), i);
        }

        int numNodes = nodes.size();
        double[][] distances = new double[numNodes][numNodes];
        Node[][] nextNode = new Node[numNodes][numNodes];

        // Initialize distances and nextNode matrix
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                if (i == j) {
                    distances[i][j] = 0;
                } else {
                    distances[i][j] = Double.POSITIVE_INFINITY;
                }
                nextNode[i][j] = null;
            }
        }

        // Fill initial distances based on direct edges
        for (Node node : nodes) {
            int u = nodeToIndex.get(node);
            for (Map.Entry<Node, Double> neighborEntry : node.getNeighbors().entrySet()) {
                Node neighbor = neighborEntry.getKey();
                double weight = neighborEntry.getValue();
                int v = nodeToIndex.get(neighbor);
                distances[u][v] = weight;
                nextNode[u][v] = neighbor;
            }
        }

        // Apply Floyd-Warshall algorithm
        for (int k = 0; k < numNodes; k++) {
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < numNodes; j++) {
                    if (distances[i][k] != Double.POSITIVE_INFINITY && distances[k][j] != Double.POSITIVE_INFINITY) {
                        if (distances[i][k] + distances[k][j] < distances[i][j]) {
                            distances[i][j] = distances[i][k] + distances[k][j];
                            nextNode[i][j] = nextNode[i][k];
                        }
                    }
                }
            }
        }

        // Check for negative cycles
        for (int i = 0; i < numNodes; i++) {
            if (distances[i][i] < 0) {
                // Negative cycle detected
                System.out.println("Floyd-Warshall: Negative cycle detected. Shortest path is undefined.");
                return new ArrayList<>();
            }
        }

        int startIndex = nodeToIndex.get(start);
        int endIndex = nodeToIndex.get(end);

        if (distances[startIndex][endIndex] == Double.POSITIVE_INFINITY) {
            return new ArrayList<>(); // No path exists
        }

        // Reconstruct path
        List<Node> path = new ArrayList<>();
        path.add(start);
        Node current = start;

        while (current != null && !current.equals(end)) {
            int u = nodeToIndex.get(current);
            Node next = nextNode[u][endIndex];
            if (next == null) {
                // Should not happen if a path exists, but as a safeguard
                return new ArrayList<>();
            }
            path.add(next);
            current = next;
        }

        return path;
    }
} 