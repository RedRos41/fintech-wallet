package com.uniquindio.fintech.datastructures.list;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Lista enlazada simple genérica.
 * <p>Estructura de datos lineal donde cada nodo apunta al siguiente.</p>
 *
 * @param <T> tipo de los elementos almacenados
 */
public class SimpleLinkedList<T> implements Iterable<T> {

    /**
     * Nodo interno de la lista enlazada simple.
     */
    static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    /**
     * Crea una lista enlazada simple vacía.
     */
    public SimpleLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Agrega un elemento al inicio de la lista.
     * <p>Complejidad: O(1)</p>
     *
     * @param data el elemento a agregar
     */
    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = head;
        head = newNode;
        if (tail == null) {
            tail = newNode;
        }
        size++;
    }

    /**
     * Agrega un elemento al final de la lista.
     * <p>Complejidad: O(1)</p>
     *
     * @param data el elemento a agregar
     */
    public void addLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    /**
     * Inserta un elemento en la posición indicada.
     * <p>Complejidad: O(n)</p>
     *
     * @param index posición donde insertar
     * @param data  el elemento a insertar
     * @throws IndexOutOfBoundsException si el índice está fuera de rango
     */
    public void add(int index, T data) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
        if (index == 0) {
            addFirst(data);
            return;
        }
        if (index == size) {
            addLast(data);
            return;
        }
        Node<T> prev = getNode(index - 1);
        Node<T> newNode = new Node<>(data);
        newNode.next = prev.next;
        prev.next = newNode;
        size++;
    }

    /**
     * Elimina y retorna el primer elemento de la lista.
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento eliminado
     * @throws NoSuchElementException si la lista está vacía
     */
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        T data = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return data;
    }

    /**
     * Elimina y retorna el último elemento de la lista.
     * <p>Complejidad: O(n)</p>
     *
     * @return el elemento eliminado
     * @throws NoSuchElementException si la lista está vacía
     */
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        if (size == 1) {
            return removeFirst();
        }
        Node<T> prev = getNode(size - 2);
        T data = tail.data;
        prev.next = null;
        tail = prev;
        size--;
        return data;
    }

    /**
     * Elimina y retorna el elemento en la posición indicada.
     * <p>Complejidad: O(n)</p>
     *
     * @param index la posición del elemento a eliminar
     * @return el elemento eliminado
     * @throws IndexOutOfBoundsException si el índice está fuera de rango
     */
    public T remove(int index) {
        checkIndex(index);
        if (index == 0) {
            return removeFirst();
        }
        if (index == size - 1) {
            return removeLast();
        }
        Node<T> prev = getNode(index - 1);
        T data = prev.next.data;
        prev.next = prev.next.next;
        size--;
        return data;
    }

    /**
     * Obtiene el elemento en la posición indicada.
     * <p>Complejidad: O(n)</p>
     *
     * @param index la posición del elemento
     * @return el elemento en la posición indicada
     * @throws IndexOutOfBoundsException si el índice está fuera de rango
     */
    public T get(int index) {
        checkIndex(index);
        return getNode(index).data;
    }

    /**
     * Obtiene el primer elemento de la lista.
     * <p>Complejidad: O(1)</p>
     *
     * @return el primer elemento
     * @throws NoSuchElementException si la lista está vacía
     */
    public T getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        return head.data;
    }

    /**
     * Obtiene el último elemento de la lista.
     * <p>Complejidad: O(1)</p>
     *
     * @return el último elemento
     * @throws NoSuchElementException si la lista está vacía
     */
    public T getLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        return tail.data;
    }

    /**
     * Verifica si la lista contiene el elemento dado.
     * <p>Complejidad: O(n)</p>
     *
     * @param data el elemento a buscar
     * @return true si el elemento está en la lista
     */
    public boolean contains(T data) {
        return indexOf(data) >= 0;
    }

    /**
     * Retorna el índice de la primera ocurrencia del elemento, o -1 si no existe.
     * <p>Complejidad: O(n)</p>
     *
     * @param data el elemento a buscar
     * @return el índice del elemento o -1
     */
    public int indexOf(T data) {
        Node<T> current = head;
        for (int i = 0; i < size; i++) {
            if (Objects.equals(current.data, data)) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    /**
     * Retorna el número de elementos en la lista.
     * <p>Complejidad: O(1)</p>
     *
     * @return el tamaño de la lista
     */
    public int size() {
        return size;
    }

    /**
     * Verifica si la lista está vacía.
     * <p>Complejidad: O(1)</p>
     *
     * @return true si la lista no contiene elementos
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Elimina todos los elementos de la lista.
     * <p>Complejidad: O(1)</p>
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Retorna un iterador sobre los elementos de la lista.
     * <p>Complejidad: O(1) para creación, O(n) para recorrido completo</p>
     *
     * @return un iterador
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No hay más elementos");
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    /**
     * Convierte esta lista a una java.util.ArrayList.
     * <p>Complejidad: O(n)</p>
     *
     * @return una lista de java.util con los mismos elementos
     */
    public java.util.List<T> toJavaList() {
        java.util.List<T> list = new java.util.ArrayList<>(size);
        for (T item : this) {
            list.add(item);
        }
        return list;
    }

    // --- Métodos auxiliares privados ---

    private Node<T> getNode(int index) {
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
    }
}
