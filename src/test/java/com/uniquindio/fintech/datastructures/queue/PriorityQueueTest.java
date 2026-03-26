package com.uniquindio.fintech.datastructures.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class PriorityQueueTest {

    private PriorityQueue<Integer> pq;

    @BeforeEach
    void setUp() {
        pq = new PriorityQueue<>();
    }

    @Test
    void enqueuesMaintainOrder() {
        pq.enqueue(30);
        pq.enqueue(10);
        pq.enqueue(20);
        assertEquals(10, pq.peek());
        assertEquals(3, pq.size());
    }

    @Test
    void dequeueReturnsSmallestFirst() {
        pq.enqueue(50);
        pq.enqueue(20);
        pq.enqueue(40);
        pq.enqueue(10);
        pq.enqueue(30);
        assertEquals(10, pq.dequeue());
        assertEquals(20, pq.dequeue());
        assertEquals(30, pq.dequeue());
        assertEquals(40, pq.dequeue());
        assertEquals(50, pq.dequeue());
    }

    @Test
    void dequeueOnEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> pq.dequeue());
    }

    @Test
    void peekOnEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> pq.peek());
    }

    @Test
    void clearAndIsEmpty() {
        assertTrue(pq.isEmpty());
        pq.enqueue(5);
        assertFalse(pq.isEmpty());
        pq.clear();
        assertTrue(pq.isEmpty());
    }

    @Test
    void duplicateValuesAllowed() {
        pq.enqueue(10);
        pq.enqueue(10);
        pq.enqueue(5);
        assertEquals(5, pq.dequeue());
        assertEquals(10, pq.dequeue());
        assertEquals(10, pq.dequeue());
    }

    @Test
    void stringPriority() {
        PriorityQueue<String> spq = new PriorityQueue<>();
        spq.enqueue("Charlie");
        spq.enqueue("Alpha");
        spq.enqueue("Bravo");
        assertEquals("Alpha", spq.dequeue());
        assertEquals("Bravo", spq.dequeue());
        assertEquals("Charlie", spq.dequeue());
    }
}
