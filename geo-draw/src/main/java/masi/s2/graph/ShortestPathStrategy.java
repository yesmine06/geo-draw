package masi.s2.graph;

import java.util.List;

public interface ShortestPathStrategy {
    /**
     * Trouve le chemin le plus court entre deux nœuds dans un graphe
     * @param graph Le graphe à analyser
     * @param start Le nœud de départ
     * @param end Le nœud d'arrivée
     * @return Une liste de nœuds représentant le chemin le plus court
     * @throws IllegalArgumentException si les paramètres sont invalides
     * @throws PathNotFoundException si aucun chemin n'est trouvé
     */
    List<Node> findShortestPath(Graph graph, Node start, Node end) throws IllegalArgumentException, PathNotFoundException;

    /**
     * Vérifie si la stratégie peut être appliquée au graphe donné
     * @param graph Le graphe à vérifier
     * @return true si la stratégie peut être appliquée, false sinon
     */
    boolean canHandle(Graph graph);

    /**
     * Retourne le nom de la stratégie
     * @return Le nom de la stratégie
     */
    String getName();

    /**
     * Retourne une description de la stratégie
     * @return La description de la stratégie
     */
    String getDescription();
} 