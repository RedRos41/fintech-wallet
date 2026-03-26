package com.uniquindio.fintech.datastructures.graph;

import com.uniquindio.fintech.datastructures.hash.HashTable;
import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import com.uniquindio.fintech.datastructures.queue.Queue;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Grafo dirigido ponderado genérico.
 * <p>Utiliza listas de adyacencia implementadas con {@link HashTable} y {@link SimpleLinkedList}.</p>
 *
 * @param <T> tipo de los vértices
 */
public class Graph<T> {

    /**
     * Arista interna del grafo con destino, peso y etiqueta opcional.
     */
    public static class Edge<T> {
        private final T destination;
        private final double weight;
        private final String label;

        /**
         * Crea una arista con destino, peso y etiqueta.
         *
         * @param destination vértice destino
         * @param weight      peso de la arista
         * @param label       etiqueta opcional
         */
        public Edge(T destination, double weight, String label) {
            this.destination = destination;
            this.weight = weight;
            this.label = label;
        }

        /**
         * Retorna el vértice destino.
         * @return destino
         */
        public T getDestination() {
            return destination;
        }

        /**
         * Retorna el peso de la arista.
         * @return peso
         */
        public double getWeight() {
            return weight;
        }

        /**
         * Retorna la etiqueta de la arista.
         * @return etiqueta
         */
        public String getLabel() {
            return label;
        }
    }

    /** Estado de vértice no visitado durante detección de ciclos. */
    private static final int ESTADO_NO_VISITADO = 0;
    /** Estado de vértice en proceso durante detección de ciclos. */
    private static final int ESTADO_EN_PROCESO = 1;
    /** Estado de vértice completado durante detección de ciclos. */
    private static final int ESTADO_COMPLETADO = 2;

    private final HashTable<T, SimpleLinkedList<Edge<T>>> adjacencyList;
    private int edgeCount;

    /**
     * Crea un grafo dirigido ponderado vacío.
     */
    public Graph() {
        this.adjacencyList = new HashTable<>();
        this.edgeCount = 0;
    }

    /**
     * Agrega un vértice al grafo.
     * <p>Complejidad: O(1) amortizado</p>
     *
     * @param vertex el vértice a agregar
     * @throws IllegalArgumentException si el vértice ya existe
     */
    public void addVertex(T vertex) {
        if (hasVertex(vertex)) {
            throw new IllegalArgumentException("El vértice ya existe: " + vertex);
        }
        adjacencyList.put(vertex, new SimpleLinkedList<>());
    }

    /**
     * Elimina un vértice y todas sus aristas asociadas.
     * <p>Complejidad: O(V + E)</p>
     *
     * @param vertex el vértice a eliminar
     * @throws NoSuchElementException si el vértice no existe
     */
    public void removeVertex(T vertex) {
        if (!hasVertex(vertex)) {
            throw new NoSuchElementException("El vértice no existe: " + vertex);
        }
        edgeCount -= adjacencyList.get(vertex).size();
        adjacencyList.remove(vertex);
        removeEdgesTo(vertex);
    }

    /**
     * Agrega una arista dirigida con peso entre dos vértices (sin etiqueta).
     * <p>Complejidad: O(k) donde k es el grado del vértice origen</p>
     *
     * @param from   vértice origen
     * @param to     vértice destino
     * @param weight peso de la arista
     * @throws NoSuchElementException   si alguno de los vértices no existe
     * @throws IllegalArgumentException si la arista ya existe
     */
    public void addEdge(T from, T to, double weight) {
        addEdgeWithLabel(from, to, weight, "");
    }

    /**
     * Agrega una arista dirigida con peso y etiqueta entre dos vértices.
     * <p>Complejidad: O(k) donde k es el grado del vértice origen</p>
     *
     * @param from   vértice origen
     * @param to     vértice destino
     * @param weight peso de la arista
     * @param label  etiqueta de la arista
     * @throws NoSuchElementException   si alguno de los vértices no existe
     * @throws IllegalArgumentException si la arista ya existe
     */
    public void addEdgeWithLabel(T from, T to, double weight, String label) {
        validateVertex(from);
        validateVertex(to);
        if (hasEdge(from, to)) {
            throw new IllegalArgumentException("La arista ya existe: " + from + " -> " + to);
        }
        adjacencyList.get(from).addLast(new Edge<>(to, weight, label));
        edgeCount++;
    }

    /**
     * Elimina la arista entre dos vértices.
     * <p>Complejidad: O(k) donde k es el grado del vértice origen</p>
     *
     * @param from vértice origen
     * @param to   vértice destino
     * @throws NoSuchElementException si la arista no existe
     */
    public void removeEdge(T from, T to) {
        validateVertex(from);
        SimpleLinkedList<Edge<T>> edges = adjacencyList.get(from);
        int index = findEdgeIndex(edges, to);
        if (index < 0) {
            throw new NoSuchElementException("La arista no existe: " + from + " -> " + to);
        }
        edges.remove(index);
        edgeCount--;
    }

    /**
     * Retorna los vecinos (destinos de aristas salientes) de un vértice.
     * <p>Complejidad: O(k) donde k es el grado del vértice</p>
     *
     * @param vertex el vértice
     * @return lista de vértices vecinos
     * @throws NoSuchElementException si el vértice no existe
     */
    public SimpleLinkedList<T> getNeighbors(T vertex) {
        validateVertex(vertex);
        SimpleLinkedList<T> neighbors = new SimpleLinkedList<>();
        for (Edge<T> edge : adjacencyList.get(vertex)) {
            neighbors.addLast(edge.destination);
        }
        return neighbors;
    }

    /**
     * Retorna todos los vértices del grafo.
     * <p>Complejidad: O(V)</p>
     *
     * @return lista de vértices
     */
    public SimpleLinkedList<T> getVertices() {
        return adjacencyList.keys();
    }

    /**
     * Verifica si un vértice existe en el grafo.
     * <p>Complejidad: O(1) amortizado</p>
     *
     * @param vertex el vértice a verificar
     * @return true si el vértice existe
     */
    public boolean hasVertex(T vertex) {
        return adjacencyList.containsKey(vertex);
    }

    /**
     * Verifica si existe una arista entre dos vértices.
     * <p>Complejidad: O(k) donde k es el grado del vértice origen</p>
     *
     * @param from vértice origen
     * @param to   vértice destino
     * @return true si la arista existe
     */
    public boolean hasEdge(T from, T to) {
        if (!hasVertex(from) || !hasVertex(to)) return false;
        return findEdgeIndex(adjacencyList.get(from), to) >= 0;
    }

    /**
     * Obtiene el peso de la arista entre dos vértices.
     * <p>Complejidad: O(k) donde k es el grado del vértice origen</p>
     *
     * @param from vértice origen
     * @param to   vértice destino
     * @return el peso de la arista
     * @throws NoSuchElementException si la arista no existe
     */
    public double getEdgeWeight(T from, T to) {
        validateVertex(from);
        for (Edge<T> edge : adjacencyList.get(from)) {
            if (Objects.equals(edge.destination, to)) {
                return edge.weight;
            }
        }
        throw new NoSuchElementException("La arista no existe: " + from + " -> " + to);
    }

    /**
     * Realiza un recorrido en anchura (BFS) desde un vértice inicial.
     * <p>Complejidad: O(V + E)</p>
     *
     * @param start el vértice de inicio
     * @return lista con los vértices en orden BFS
     * @throws NoSuchElementException si el vértice no existe
     */
    public SimpleLinkedList<T> bfs(T start) {
        validateVertex(start);
        SimpleLinkedList<T> result = new SimpleLinkedList<>();
        HashTable<T, Boolean> visited = new HashTable<>();
        Queue<T> queue = new Queue<>();
        visited.put(start, true);
        queue.enqueue(start);
        while (!queue.isEmpty()) {
            T current = queue.dequeue();
            result.addLast(current);
            for (Edge<T> edge : adjacencyList.get(current)) {
                if (!visited.containsKey(edge.destination)) {
                    visited.put(edge.destination, true);
                    queue.enqueue(edge.destination);
                }
            }
        }
        return result;
    }

    /**
     * Realiza un recorrido en profundidad (DFS) desde un vértice inicial.
     * <p>Complejidad: O(V + E)</p>
     *
     * @param start el vértice de inicio
     * @return lista con los vértices en orden DFS
     * @throws NoSuchElementException si el vértice no existe
     */
    public SimpleLinkedList<T> dfs(T start) {
        validateVertex(start);
        SimpleLinkedList<T> result = new SimpleLinkedList<>();
        HashTable<T, Boolean> visited = new HashTable<>();
        dfsRec(start, visited, result);
        return result;
    }

    /**
     * Verifica si el grafo contiene ciclos.
     * <p>Complejidad: O(V + E)</p>
     *
     * @return true si el grafo contiene al menos un ciclo
     */
    public boolean hasCycles() {
        HashTable<T, Integer> state = new HashTable<>();
        SimpleLinkedList<T> vertices = getVertices();
        for (T v : vertices) {
            state.put(v, ESTADO_NO_VISITADO);
        }
        for (T v : vertices) {
            if (state.get(v) == ESTADO_NO_VISITADO && hasCycleFrom(v, state)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna el número de vértices en el grafo.
     * <p>Complejidad: O(1)</p>
     *
     * @return cantidad de vértices
     */
    public int vertexCount() {
        return adjacencyList.size();
    }

    /**
     * Retorna el número de aristas en el grafo.
     * <p>Complejidad: O(1)</p>
     *
     * @return cantidad de aristas
     */
    public int edgeCount() {
        return edgeCount;
    }

    // --- Métodos auxiliares privados ---

    private void validateVertex(T vertex) {
        if (!hasVertex(vertex)) {
            throw new NoSuchElementException("El vértice no existe: " + vertex);
        }
    }

    private int findEdgeIndex(SimpleLinkedList<Edge<T>> edges, T destination) {
        int i = 0;
        for (Edge<T> edge : edges) {
            if (Objects.equals(edge.destination, destination)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private void removeEdgesTo(T vertex) {
        SimpleLinkedList<T> vertices = adjacencyList.keys();
        for (T v : vertices) {
            SimpleLinkedList<Edge<T>> edges = adjacencyList.get(v);
            int index = findEdgeIndex(edges, vertex);
            while (index >= 0) {
                edges.remove(index);
                edgeCount--;
                index = findEdgeIndex(edges, vertex);
            }
        }
    }

    private void dfsRec(T vertex, HashTable<T, Boolean> visited, SimpleLinkedList<T> result) {
        visited.put(vertex, true);
        result.addLast(vertex);
        for (Edge<T> edge : adjacencyList.get(vertex)) {
            if (!visited.containsKey(edge.destination)) {
                dfsRec(edge.destination, visited, result);
            }
        }
    }

    /**
     * Detecta ciclos desde un vértice usando estados: no visitado, en proceso, completado.
     */
    private boolean hasCycleFrom(T vertex, HashTable<T, Integer> state) {
        state.put(vertex, ESTADO_EN_PROCESO);
        for (Edge<T> edge : adjacencyList.get(vertex)) {
            int neighborState = state.get(edge.destination);
            if (neighborState == ESTADO_EN_PROCESO) return true;
            if (neighborState == ESTADO_NO_VISITADO && hasCycleFrom(edge.destination, state)) {
                return true;
            }
        }
        state.put(vertex, ESTADO_COMPLETADO);
        return false;
    }
}
