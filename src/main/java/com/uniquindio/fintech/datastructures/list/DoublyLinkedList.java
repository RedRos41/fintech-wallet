package com.uniquindio.fintech.datastructures.list;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Lista doblemente enlazada genérica.
 * <p>Cada nodo tiene referencia al siguiente y al anterior, permitiendo recorrido bidireccional.</p>
 *
 * @param <T> tipo de los elementos almacenados
 */
public class DoublyLinkedList<T> implements Iterable<T> {

    /**
     * Nodo interno de la lista doblemente enlazada.
     */
    static class DNode<T> {
        T data;
        DNode<T> next;
        DNode<T> prev;

        DNode(T data) {
            this.data = data;
        }
    }

    private DNode<T> head;
    private DNode<T> tail;
    private int size;

    /**
     * Crea una lista doblemente enlazada vacía.
     */
    public DoublyLinkedList() {
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
        DNode<T> newNode = new DNode<>(data);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
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
        DNode<T> newNode = new DNode<>(data);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.prev = tail;
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
        DNode<T> current = getNode(index);
        DNode<T> newNode = new DNode<>(data);
        newNode.prev = current.prev;
        newNode.next = current;
        current.prev.next = newNode;
        current.prev = newNode;
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
        } else {
            head.prev = null;
        }
        size--;
        return data;
    }

    /**
     * Elimina y retorna el último elemento de la lista.
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento eliminado
     * @throws NoSuchElementException si la lista está vacía
     */
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        T data = tail.data;
        tail = tail.prev;
        if (tail == null) {
            head = null;
        } else {
            tail.next = null;
        }
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
        DNode<T> node = getNode(index);
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
        return node.data;
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
        DNode<T> current = head;
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
     * Retorna un iterador que recorre la lista de inicio a fin.
     * <p>Complejidad: O(1) para creación, O(n) para recorrido completo</p>
     *
     * @return un iterador hacia adelante
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private DNode<T> current = head;

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
     * Retorna un iterador que recorre la lista de fin a inicio.
     * <p>Complejidad: O(1) para creación, O(n) para recorrido completo</p>
     *
     * @return un iterador en reversa
     */
    public Iterator<T> iteratorReverse() {
        return new Iterator<>() {
            private DNode<T> current = tail;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No hay más elementos en reversa");
                }
                T data = current.data;
                current = current.prev;
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

    private DNode<T> getNode(int index) {
        DNode<T> current;
        if (index < size / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
    }
}
