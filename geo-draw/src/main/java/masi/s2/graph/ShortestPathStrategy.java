package masi.s2.graph;

import java.util.List;

public interface ShortestPathStrategy {
    List<Node> findShortestPath(Graph graph, Node start, Node end);
} 