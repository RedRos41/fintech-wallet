package com.uniquindio.fintech.datastructures.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class DoublyLinkedListTest {

    private DoublyLinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new DoublyLinkedList<>();
    }

    @Test
    void addFirstAndLast() {
        list.addFirst("B");
        list.addFirst("A");
        list.addLast("C");
        assertEquals("A", list.getFirst());
        assertEquals("C", list.getLast());
        assertEquals(3, list.size());
    }

    @Test
    void addAtIndex() {
        list.addLast("A");
        list.addLast("C");
        list.add(1, "B");
        assertEquals("B", list.get(1));
        assertEquals(3, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(5, "X"));
    }

    @Test
    void removeFirstAndLast() {
        list.addLast("A");
        list.addLast("B");
        list.addLast("C");
        assertEquals("A", list.removeFirst());
        assertEquals("C", list.removeLast());
        assertEquals(1, list.size());
        assertEquals("B", list.getFirst());
    }

    @Test
    void removeFromEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> list.removeFirst());
        assertThrows(NoSuchElementException.class, () -> list.removeLast());
    }

    @Test
    void removeAtMiddleIndex() {
        list.addLast("A");
        list.addLast("B");
        list.addLast("C");
        list.addLast("D");
        assertEquals("B", list.remove(1));
        assertEquals(3, list.size());
        assertEquals("C", list.get(1));
    }

    @Test
    void containsAndIndexOf() {
        list.addLast("hello");
        list.addLast("world");
        assertTrue(list.contains("hello"));
        assertFalse(list.contains("missing"));
        assertEquals(1, list.indexOf("world"));
        assertEquals(-1, list.indexOf("missing"));
    }

    @Test
    void iteratorForward() {
        list.addLast("A");
        list.addLast("B");
        list.addLast("C");
        Iterator<String> it = list.iterator();
        assertEquals("A", it.next());
        assertEquals("B", it.next());
        assertEquals("C", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void iteratorReverse() {
        list.addLast("A");
        list.addLast("B");
        list.addLast("C");
        Iterator<String> it = list.iteratorReverse();
        assertEquals("C", it.next());
        assertEquals("B", it.next());
        assertEquals("A", it.next());
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void clearAndToJavaList() {
        list.addLast("X");
        list.addLast("Y");
        assertEquals(2, list.toJavaList().size());
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void singleElementEdgeCase() {
        list.addFirst("only");
        assertEquals("only", list.getFirst());
        assertEquals("only", list.getLast());
        assertEquals("only", list.removeFirst());
        assertTrue(list.isEmpty());
    }
}
