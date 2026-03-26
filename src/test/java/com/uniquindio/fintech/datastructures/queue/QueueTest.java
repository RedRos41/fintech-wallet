package com.uniquindio.fintech.datastructures.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class QueueTest {

    private Queue<String> queue;

    @BeforeEach
    void setUp() {
        queue = new Queue<>();
    }

    @Test
    void enqueueAndPeek() {
        queue.enqueue("first");
        queue.enqueue("second");
        assertEquals("first", queue.peek());
        assertEquals(2, queue.size());
    }

    @Test
    void dequeueInFifoOrder() {
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");
        assertEquals("A", queue.dequeue());
        assertEquals("B", queue.dequeue());
        assertEquals("C", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void dequeueOnEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> queue.dequeue());
    }

    @Test
    void peekOnEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> queue.peek());
    }

    @Test
    void clearResetsQueue() {
        queue.enqueue("X");
        queue.enqueue("Y");
        queue.clear();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    void iteratorTraversal() {
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        StringBuilder sb = new StringBuilder();
        for (String s : queue) {
            sb.append(s);
        }
        assertEquals("123", sb.toString());
    }

    @Test
    void mixedEnqueueDequeue() {
        queue.enqueue("A");
        queue.enqueue("B");
        assertEquals("A", queue.dequeue());
        queue.enqueue("C");
        assertEquals("B", queue.dequeue());
        assertEquals("C", queue.dequeue());
        assertTrue(queue.isEmpty());
    }
}
