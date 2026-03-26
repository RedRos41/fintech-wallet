package com.uniquindio.fintech.datastructures.hash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    private HashTable<String, Integer> table;

    @BeforeEach
    void setUp() {
        table = new HashTable<>();
    }

    @Test
    void putAndGet() {
        table.put("one", 1);
        table.put("two", 2);
        assertEquals(1, table.get("one"));
        assertEquals(2, table.get("two"));
        assertEquals(2, table.size());
    }

    @Test
    void putOverwritesValue() {
        table.put("key", 100);
        table.put("key", 200);
        assertEquals(200, table.get("key"));
        assertEquals(1, table.size());
    }

    @Test
    void getOnMissingKeyThrows() {
        assertThrows(NoSuchElementException.class, () -> table.get("missing"));
    }

    @Test
    void removeExistingKey() {
        table.put("A", 1);
        table.put("B", 2);
        assertEquals(1, table.remove("A"));
        assertEquals(1, table.size());
        assertFalse(table.containsKey("A"));
    }

    @Test
    void removeMissingKeyThrows() {
        assertThrows(NoSuchElementException.class, () -> table.remove("missing"));
    }

    @Test
    void containsKey() {
        table.put("present", 42);
        assertTrue(table.containsKey("present"));
        assertFalse(table.containsKey("absent"));
    }

    @Test
    void keysAndValues() {
        table.put("a", 1);
        table.put("b", 2);
        table.put("c", 3);
        assertEquals(3, table.keys().size());
        assertEquals(3, table.values().size());
    }

    @Test
    void clearAndIsEmpty() {
        assertTrue(table.isEmpty());
        table.put("x", 10);
        assertFalse(table.isEmpty());
        table.clear();
        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
    }

    @Test
    void autoResizeOnHighLoad() {
        for (int i = 0; i < 50; i++) {
            table.put("key" + i, i);
        }
        assertEquals(50, table.size());
        for (int i = 0; i < 50; i++) {
            assertEquals(i, table.get("key" + i));
        }
    }

    @Test
    void nullKeyThrows() {
        assertThrows(IllegalArgumentException.class, () -> table.put(null, 1));
        assertThrows(IllegalArgumentException.class, () -> table.get(null));
        assertThrows(IllegalArgumentException.class, () -> table.remove(null));
    }
}
