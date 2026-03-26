package com.uniquindio.fintech.datastructures.tree;

import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;

import java.util.NoSuchElementException;

/**
 * Árbol binario de búsqueda genérico.
 * <p>Cada nodo tiene como máximo dos hijos. Para todo nodo, los valores del
 * subárbol izquierdo son menores y los del subárbol derecho son mayores.</p>
 *
 * @param <T> tipo de los elementos, debe ser comparable
 */
public class BinarySearchTree<T extends Comparable<T>> {

    /**
     * Nodo interno del árbol binario de búsqueda.
     */
    static class BSTNode<T> {
        T data;
        BSTNode<T> left;
        BSTNode<T> right;

        BSTNode(T data) {
            this.data = data;
        }
    }

    private BSTNode<T> root;
    private int size;

    /**
     * Crea un árbol binario de búsqueda vacío.
     */
    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Inserta un elemento en el árbol.
     * <p>Complejidad: O(log n) promedio, O(n) peor caso</p>
     *
     * @param data el elemento a insertar
     * @throws IllegalArgumentException si el elemento ya existe en el árbol
     */
    public void insert(T data) {
        root = insertRec(root, data);
        size++;
    }

    /**
     * Verifica si un elemento existe en el árbol.
     * <p>Complejidad: O(log n) promedio, O(n) peor caso</p>
     *
     * @param data el elemento a buscar
     * @return true si el elemento existe en el árbol
     */
    public boolean contains(T data) {
        return searchRec(root, data);
    }

    /**
     * Elimina un elemento del árbol.
     * <p>Complejidad: O(log n) promedio, O(n) peor caso</p>
     *
     * @param data el elemento a eliminar
     * @throws NoSuchElementException si el elemento no existe
     */
    public void delete(T data) {
        if (!contains(data)) {
            throw new NoSuchElementException("El elemento no existe en el árbol: " + data);
        }
        root = deleteRec(root, data);
        size--;
    }

    /**
     * Retorna el elemento mínimo del árbol.
     * <p>Complejidad: O(log n) promedio, O(n) peor caso</p>
     *
     * @return el elemento mínimo
     * @throws NoSuchElementException si el árbol está vacío
     */
    public T min() {
        if (isEmpty()) {
            throw new NoSuchElementException("El árbol está vacío");
        }
        return minNode(root).data;
    }

    /**
     * Retorna el elemento máximo del árbol.
     * <p>Complejidad: O(log n) promedio, O(n) peor caso</p>
     *
     * @return el elemento máximo
     * @throws NoSuchElementException si el árbol está vacío
     */
    public T max() {
        if (isEmpty()) {
            throw new NoSuchElementException("El árbol está vacío");
        }
        BSTNode<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.data;
    }

    /**
     * Retorna los elementos en recorrido inorden (izquierda, raíz, derecha).
     * <p>Complejidad: O(n)</p>
     *
     * @return lista con los elementos en inorden
     */
    public SimpleLinkedList<T> inOrder() {
        SimpleLinkedList<T> result = new SimpleLinkedList<>();
        inOrderRec(root, result);
        return result;
    }

    /**
     * Retorna los elementos en recorrido preorden (raíz, izquierda, derecha).
     * <p>Complejidad: O(n)</p>
     *
     * @return lista con los elementos en preorden
     */
    public SimpleLinkedList<T> preOrder() {
        SimpleLinkedList<T> result = new SimpleLinkedList<>();
        preOrderRec(root, result);
        return result;
    }

    /**
     * Retorna los elementos en recorrido postorden (izquierda, derecha, raíz).
     * <p>Complejidad: O(n)</p>
     *
     * @return lista con los elementos en postorden
     */
    public SimpleLinkedList<T> postOrder() {
        SimpleLinkedList<T> result = new SimpleLinkedList<>();
        postOrderRec(root, result);
        return result;
    }

    /**
     * Retorna los elementos dentro del rango [min, max] inclusive.
     * <p>Complejidad: O(n) peor caso, O(log n + k) promedio donde k es el número de resultados</p>
     *
     * @param min límite inferior del rango
     * @param max límite superior del rango
     * @return lista con los elementos en el rango
     */
    public SimpleLinkedList<T> rangeSearch(T min, T max) {
        SimpleLinkedList<T> result = new SimpleLinkedList<>();
        rangeSearchRec(root, min, max, result);
        return result;
    }

    /**
     * Retorna la altura del árbol.
     * <p>Complejidad: O(n)</p>
     *
     * @return la altura del árbol, -1 si está vacío
     */
    public int height() {
        return heightRec(root);
    }

    /**
     * Retorna el número de elementos en el árbol.
     * <p>Complejidad: O(1)</p>
     *
     * @return el tamaño del árbol
     */
    public int size() {
        return size;
    }

    /**
     * Verifica si el árbol está vacío.
     * <p>Complejidad: O(1)</p>
     *
     * @return true si el árbol no contiene elementos
     */
    public boolean isEmpty() {
        return size == 0;
    }

    // --- Métodos recursivos privados ---

    private BSTNode<T> insertRec(BSTNode<T> node, T data) {
        if (node == null) {
            return new BSTNode<>(data);
        }
        int cmp = data.compareTo(node.data);
        if (cmp < 0) {
            node.left = insertRec(node.left, data);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, data);
        } else {
            throw new IllegalArgumentException("El elemento ya existe en el árbol: " + data);
        }
        return node;
    }

    private boolean searchRec(BSTNode<T> node, T data) {
        if (node == null) {
            return false;
        }
        int cmp = data.compareTo(node.data);
        if (cmp < 0) {
            return searchRec(node.left, data);
        } else if (cmp > 0) {
            return searchRec(node.right, data);
        }
        return true;
    }

    private BSTNode<T> deleteRec(BSTNode<T> node, T data) {
        if (node == null) {
            return null;
        }
        int cmp = data.compareTo(node.data);
        if (cmp < 0) {
            node.left = deleteRec(node.left, data);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, data);
        } else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            BSTNode<T> successor = minNode(node.right);
            node.data = successor.data;
            node.right = deleteRec(node.right, successor.data);
        }
        return node;
    }

    private BSTNode<T> minNode(BSTNode<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private void inOrderRec(BSTNode<T> node, SimpleLinkedList<T> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.addLast(node.data);
        inOrderRec(node.right, result);
    }

    private void preOrderRec(BSTNode<T> node, SimpleLinkedList<T> result) {
        if (node == null) return;
        result.addLast(node.data);
        preOrderRec(node.left, result);
        preOrderRec(node.right, result);
    }

    private void postOrderRec(BSTNode<T> node, SimpleLinkedList<T> result) {
        if (node == null) return;
        postOrderRec(node.left, result);
        postOrderRec(node.right, result);
        result.addLast(node.data);
    }

    private void rangeSearchRec(BSTNode<T> node, T min, T max, SimpleLinkedList<T> result) {
        if (node == null) return;
        if (node.data.compareTo(min) > 0) {
            rangeSearchRec(node.left, min, max, result);
        }
        if (node.data.compareTo(min) >= 0 && node.data.compareTo(max) <= 0) {
            result.addLast(node.data);
        }
        if (node.data.compareTo(max) < 0) {
            rangeSearchRec(node.right, min, max, result);
        }
    }

    private int heightRec(BSTNode<T> node) {
        if (node == null) return -1;
        int leftH = heightRec(node.left);
        int rightH = heightRec(node.right);
        return 1 + Math.max(leftH, rightH);
    }
}
