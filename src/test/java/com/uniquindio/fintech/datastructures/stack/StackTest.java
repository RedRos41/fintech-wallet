package com.uniquindio.fintech.datastructures.stack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class StackTest {

    private Stack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new Stack<>();
    }

    @Test
    void pushAndPeek() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.peek());
        assertEquals(3, stack.size());
    }

    @Test
    void popReturnsInLifoOrder() {
        stack.push(10);
        stack.push(20);
        stack.push(30);
        assertEquals(30, stack.pop());
        assertEquals(20, stack.pop());
        assertEquals(10, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void popOnEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> stack.pop());
    }

    @Test
    void peekOnEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> stack.peek());
    }

    @Test
    void clearResetsStack() {
        stack.push(1);
        stack.push(2);
        stack.clear();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void iteratorFromTopToBottom() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        int expected = 3;
        for (int val : stack) {
            assertEquals(expected, val);
            expected--;
        }
    }

    @Test
    void sizeTracking() {
        assertEquals(0, stack.size());
        stack.push(5);
        assertEquals(1, stack.size());
        stack.pop();
        assertEquals(0, stack.size());
    }
}
