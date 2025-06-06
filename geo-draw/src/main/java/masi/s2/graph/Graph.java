package masi.s2.graph;

import javafx.scene.canvas.GraphicsContext;
import java.util.*;

public class Graph {
    private List<Node> nodes;
    private Node selectedNode;
    private Node startNode;
    private Node endNode;
    private List<Node> shortestPath;

    public Graph() {
        this.nodes = new ArrayList<>();
        this.shortestPath = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Node from, Node to, double weight) {
        from.addNeighbor(to, weight);
        to.addNeighbor(from, weight); // Pour un graphe non dirigé
    }

    public void draw(GraphicsContext gc) {
        System.out.println("Dessin du graphe avec " + nodes.size() + " nœuds");
        
        // Dessiner d'abord les arêtes
        for (Node node : nodes) {
            node.drawEdges(gc);
        }
        
        // Puis dessiner les nœuds
        for (Node node : nodes) {
            node.draw(gc);
        }
    }

    public Node getNodeAt(double x, double y) {
        for (Node node : nodes) {
            double distance = Math.sqrt(Math.pow(x - node.getX(), 2) + Math.pow(y - node.getY(), 2));
            if (distance <= Node.NODE_RADIUS) {
                return node;
            }
        }
        return null;
    }

    public void selectNode(Node node) {
        if (selectedNode != null) {
            selectedNode.setSelected(false);
        }
        selectedNode = node;
        if (node != null) {
            node.setSelected(true);
        }
    }

    public void setStartNode(Node node) {
        if (startNode != null) {
            startNode.setInPath(false);
        }
        startNode = node;
        if (node != null) {
            node.setInPath(true);
        }
    }

    public void setEndNode(Node node) {
        if (endNode != null) {
            endNode.setInPath(false);
        }
        endNode = node;
        if (node != null) {
            node.setInPath(true);
        }
    }

    public void clearPath() {
        for (Node node : nodes) {
            node.setInPath(false);
        }
        shortestPath.clear();
    }

    public void setShortestPath(List<Node> path) {
        clearPath();
        shortestPath = path;
        for (Node node : path) {
            node.setInPath(true);
        }
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public List<Node> getShortestPath() {
        return shortestPath;
    }
} 
