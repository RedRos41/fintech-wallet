package com.uniquindio.fintech.datastructures.graph;

import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    private Graph<String> graph;

    @BeforeEach
    void setUp() {
        graph = new Graph<>();
    }

    @Test
    void addVertexAndEdge() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("A", "B", 5.0);
        assertTrue(graph.hasVertex("A"));
        assertTrue(graph.hasEdge("A", "B"));
        assertFalse(graph.hasEdge("B", "A"));
        assertEquals(2, graph.vertexCount());
        assertEquals(1, graph.edgeCount());
    }

    @Test
    void addDuplicateVertexThrows() {
        graph.addVertex("A");
        assertThrows(IllegalArgumentException.class, () -> graph.addVertex("A"));
    }

    @Test
    void addDuplicateEdgeThrows() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("A", "B", 1.0);
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge("A", "B", 2.0));
    }

    @Test
    void removeVertexRemovesIncomingEdges() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("C", "B", 2.0);
        graph.removeVertex("B");
        assertFalse(graph.hasVertex("B"));
        assertEquals(0, graph.edgeCount());
    }

    @Test
    void removeNonExistentVertexThrows() {
        assertThrows(NoSuchElementException.class, () -> graph.removeVertex("X"));
    }

    @Test
    void removeEdge() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("A", "B", 3.0);
        graph.removeEdge("A", "B");
        assertFalse(graph.hasEdge("A", "B"));
        assertEquals(0, graph.edgeCount());
    }

    @Test
    void removeNonExistentEdgeThrows() {
        graph.addVertex("A");
        graph.addVertex("B");
        assertThrows(NoSuchElementException.class, () -> graph.removeEdge("A", "B"));
    }

    @Test
    void getEdgeWeight() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdgeWithLabel("A", "B", 7.5, "transfer");
        assertEquals(7.5, graph.getEdgeWeight("A", "B"));
    }

    @Test
    void getNeighbors() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 2.0);
        SimpleLinkedList<String> neighbors = graph.getNeighbors("A");
        assertEquals(2, neighbors.size());
    }

    @Test
    void bfsTraversal() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 1.0);
        graph.addEdge("B", "D", 1.0);
        SimpleLinkedList<String> bfs = graph.bfs("A");
        assertEquals(4, bfs.size());
        assertEquals("A", bfs.getFirst());
    }

    @Test
    void dfsTraversal() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("B", "C", 1.0);
        SimpleLinkedList<String> dfs = graph.dfs("A");
        assertEquals(3, dfs.size());
        assertEquals("A", dfs.getFirst());
    }

    @Test
    void detectCyclesNoCycle() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("B", "C", 1.0);
        assertFalse(graph.hasCycles());
    }

    @Test
    void detectCyclesWithCycle() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("B", "C", 1.0);
        graph.addEdge("C", "A", 1.0);
        assertTrue(graph.hasCycles());
    }

    @Test
    void operationsOnNonExistentVertexThrows() {
        assertThrows(NoSuchElementException.class, () -> graph.bfs("X"));
        assertThrows(NoSuchElementException.class, () -> graph.dfs("X"));
        assertThrows(NoSuchElementException.class, () -> graph.getNeighbors("X"));
    }
}
