package com.uniquindio.fintech.datastructures.queue;

import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cola genérica (FIFO - First In, First Out).
 * <p>Utiliza una {@link SimpleLinkedList} internamente. Encola al final, desencola al inicio.</p>
 *
 * @param <T> tipo de los elementos almacenados
 */
public class Queue<T> implements Iterable<T> {

    private final SimpleLinkedList<T> list;

    /**
     * Crea una cola vacía.
     */
    public Queue() {
        this.list = new SimpleLinkedList<>();
    }

    /**
     * Agrega un elemento al final de la cola.
     * <p>Complejidad: O(1)</p>
     *
     * @param data el elemento a agregar
     */
    public void enqueue(T data) {
        list.addLast(data);
    }

    /**
     * Elimina y retorna el elemento al frente de la cola.
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento al frente
     * @throws NoSuchElementException si la cola está vacía
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola está vacía");
        }
        return list.removeFirst();
    }

    /**
     * Retorna el elemento al frente sin eliminarlo.
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento al frente
     * @throws NoSuchElementException si la cola está vacía
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola está vacía");
        }
        return list.getFirst();
    }

    /**
     * Verifica si la cola está vacía.
     * <p>Complejidad: O(1)</p>
     *
     * @return true si la cola no contiene elementos
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Retorna el número de elementos en la cola.
     * <p>Complejidad: O(1)</p>
     *
     * @return el tamaño de la cola
     */
    public int size() {
        return list.size();
    }

    /**
     * Elimina todos los elementos de la cola.
     * <p>Complejidad: O(1)</p>
     */
    public void clear() {
        list.clear();
    }

    /**
     * Retorna un iterador sobre los elementos de la cola (del frente al final).
     * <p>Complejidad: O(1) para creación, O(n) para recorrido completo</p>
     *
     * @return un iterador
     */
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
