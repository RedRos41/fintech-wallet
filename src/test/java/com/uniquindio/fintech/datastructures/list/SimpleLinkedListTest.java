package com.uniquindio.fintech.datastructures.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SimpleLinkedListTest {

    private SimpleLinkedList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new SimpleLinkedList<>();
    }

    @Test
    void addFirstAndGetFirst() {
        list.addFirst(10);
        list.addFirst(20);
        assertEquals(20, list.getFirst());
        assertEquals(10, list.getLast());
        assertEquals(2, list.size());
    }

    @Test
    void addLastAndGetLast() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        assertEquals(1, list.getFirst());
        assertEquals(3, list.getLast());
        assertEquals(3, list.size());
    }

    @Test
    void addAtIndex() {
        list.addLast(1);
        list.addLast(3);
        list.add(1, 2);
        assertEquals(2, list.get(1));
        assertEquals(3, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(10, 0));
    }

    @Test
    void removeFirstFromEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> list.removeFirst());
    }

    @Test
    void removeLastFromEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> list.removeLast());
    }

    @Test
    void removeAtIndex() {
        list.addLast(10);
        list.addLast(20);
        list.addLast(30);
        assertEquals(20, list.remove(1));
        assertEquals(2, list.size());
        assertEquals(30, list.get(1));
    }

    @Test
    void containsAndIndexOf() {
        list.addLast(5);
        list.addLast(10);
        list.addLast(15);
        assertTrue(list.contains(10));
        assertFalse(list.contains(99));
        assertEquals(1, list.indexOf(10));
        assertEquals(-1, list.indexOf(99));
    }

    @Test
    void clearAndIsEmpty() {
        assertTrue(list.isEmpty());
        list.addLast(1);
        assertFalse(list.isEmpty());
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void iteratorTraversal() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        Iterator<Integer> it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertEquals(3, it.next());
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void toJavaListConversion() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        List<Integer> javaList = list.toJavaList();
        assertEquals(3, javaList.size());
        assertEquals(1, javaList.get(0));
        assertEquals(3, javaList.get(2));
    }

    @Test
    void getFirstAndLastOnEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> list.getFirst());
        assertThrows(NoSuchElementException.class, () -> list.getLast());
    }

    @Test
    void singleElementOperations() {
        list.addFirst(42);
        assertEquals(42, list.removeFirst());
        assertTrue(list.isEmpty());
        list.addLast(99);
        assertEquals(99, list.removeLast());
        assertTrue(list.isEmpty());
    }
}
