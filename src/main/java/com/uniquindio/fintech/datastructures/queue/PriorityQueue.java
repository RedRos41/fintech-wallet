package com.uniquindio.fintech.datastructures.queue;

import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cola de prioridad genérica.
 * <p>Los elementos se insertan en orden (menor valor = mayor prioridad).
 * Utiliza una {@link SimpleLinkedList} con inserción ordenada.</p>
 *
 * @param <T> tipo de los elementos, debe ser comparable
 */
public class PriorityQueue<T extends Comparable<T>> implements Iterable<T> {

    private final SimpleLinkedList<T> list;

    /**
     * Crea una cola de prioridad vacía.
     */
    public PriorityQueue() {
        this.list = new SimpleLinkedList<>();
    }

    /**
     * Inserta un elemento en su posición correcta según prioridad (menor = mayor prioridad).
     * <p>Complejidad: O(n)</p>
     *
     * @param data el elemento a insertar
     */
    public void enqueue(T data) {
        if (list.isEmpty() || data.compareTo(list.getLast()) >= 0) {
            list.addLast(data);
            return;
        }
        if (data.compareTo(list.getFirst()) <= 0) {
            list.addFirst(data);
            return;
        }
        int index = findInsertionIndex(data);
        list.add(index, data);
    }

    /**
     * Elimina y retorna el elemento con mayor prioridad (menor valor).
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento con mayor prioridad
     * @throws NoSuchElementException si la cola está vacía
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola de prioridad está vacía");
        }
        return list.removeFirst();
    }

    /**
     * Retorna el elemento con mayor prioridad sin eliminarlo.
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento con mayor prioridad
     * @throws NoSuchElementException si la cola está vacía
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola de prioridad está vacía");
        }
        return list.getFirst();
    }

    /**
     * Verifica si la cola de prioridad está vacía.
     * <p>Complejidad: O(1)</p>
     *
     * @return true si no contiene elementos
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Retorna el número de elementos en la cola de prioridad.
     * <p>Complejidad: O(1)</p>
     *
     * @return el tamaño de la cola
     */
    public int size() {
        return list.size();
    }

    /**
     * Elimina todos los elementos de la cola de prioridad.
     * <p>Complejidad: O(1)</p>
     */
    public void clear() {
        list.clear();
    }

    /**
     * Retorna un iterador sobre los elementos en orden de prioridad.
     * <p>Complejidad: O(1) para creación, O(n) para recorrido completo</p>
     *
     * @return un iterador
     */
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    // --- Método auxiliar privado ---

    private int findInsertionIndex(T data) {
        int index = 0;
        for (T item : list) {
            if (data.compareTo(item) < 0) {
                return index;
            }
            index++;
        }
        return index;
    }
}
