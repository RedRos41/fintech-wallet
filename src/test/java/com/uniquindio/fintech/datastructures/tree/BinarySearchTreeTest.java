package com.uniquindio.fintech.datastructures.tree;

import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTreeTest {

    private BinarySearchTree<Integer> bst;

    @BeforeEach
    void setUp() {
        bst = new BinarySearchTree<>();
    }

    @Test
    void insertAndContains() {
        bst.insert(50);
        bst.insert(30);
        bst.insert(70);
        assertTrue(bst.contains(50));
        assertTrue(bst.contains(30));
        assertFalse(bst.contains(99));
        assertEquals(3, bst.size());
    }

    @Test
    void insertDuplicateThrows() {
        bst.insert(10);
        assertThrows(IllegalArgumentException.class, () -> bst.insert(10));
    }

    @Test
    void deleteLeafNode() {
        bst.insert(50);
        bst.insert(30);
        bst.insert(70);
        bst.delete(30);
        assertFalse(bst.contains(30));
        assertEquals(2, bst.size());
    }

    @Test
    void deleteNodeWithTwoChildren() {
        bst.insert(50);
        bst.insert(30);
        bst.insert(70);
        bst.insert(60);
        bst.insert(80);
        bst.delete(70);
        assertFalse(bst.contains(70));
        assertTrue(bst.contains(60));
        assertTrue(bst.contains(80));
    }

    @Test
    void deleteNonExistentThrows() {
        bst.insert(10);
        assertThrows(NoSuchElementException.class, () -> bst.delete(99));
    }

    @Test
    void minAndMax() {
        bst.insert(50);
        bst.insert(20);
        bst.insert(80);
        bst.insert(10);
        bst.insert(90);
        assertEquals(10, bst.min());
        assertEquals(90, bst.max());
    }

    @Test
    void minAndMaxOnEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> bst.min());
        assertThrows(NoSuchElementException.class, () -> bst.max());
    }

    @Test
    void inOrderTraversal() {
        bst.insert(50);
        bst.insert(30);
        bst.insert(70);
        bst.insert(20);
        bst.insert(40);
        List<Integer> inOrder = bst.inOrder().toJavaList();
        assertEquals(List.of(20, 30, 40, 50, 70), inOrder);
    }

    @Test
    void preOrderTraversal() {
        bst.insert(50);
        bst.insert(30);
        bst.insert(70);
        List<Integer> preOrder = bst.preOrder().toJavaList();
        assertEquals(List.of(50, 30, 70), preOrder);
    }

    @Test
    void postOrderTraversal() {
        bst.insert(50);
        bst.insert(30);
        bst.insert(70);
        List<Integer> postOrder = bst.postOrder().toJavaList();
        assertEquals(List.of(30, 70, 50), postOrder);
    }

    @Test
    void rangeSearch() {
        bst.insert(50);
        bst.insert(30);
        bst.insert(70);
        bst.insert(20);
        bst.insert(40);
        bst.insert(60);
        bst.insert(80);
        List<Integer> range = bst.rangeSearch(30, 60).toJavaList();
        assertEquals(List.of(30, 40, 50, 60), range);
    }

    @Test
    void heightCalculation() {
        assertEquals(-1, bst.height());
        bst.insert(50);
        assertEquals(0, bst.height());
        bst.insert(30);
        bst.insert(70);
        assertEquals(1, bst.height());
    }

    @Test
    void emptyTreeOperations() {
        assertTrue(bst.isEmpty());
        assertEquals(0, bst.size());
        assertFalse(bst.contains(1));
        assertTrue(bst.inOrder().isEmpty());
    }
}
