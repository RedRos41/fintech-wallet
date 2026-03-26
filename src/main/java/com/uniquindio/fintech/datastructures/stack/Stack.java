package com.uniquindio.fintech.datastructures.stack;

import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Pila genérica (LIFO - Last In, First Out).
 * <p>Utiliza una {@link SimpleLinkedList} internamente con operaciones en la cabeza para O(1).</p>
 *
 * @param <T> tipo de los elementos almacenados
 */
public class Stack<T> implements Iterable<T> {

    private final SimpleLinkedList<T> list;

    /**
     * Crea una pila vacía.
     */
    public Stack() {
        this.list = new SimpleLinkedList<>();
    }

    /**
     * Agrega un elemento en el tope de la pila.
     * <p>Complejidad: O(1)</p>
     *
     * @param data el elemento a agregar
     */
    public void push(T data) {
        list.addFirst(data);
    }

    /**
     * Elimina y retorna el elemento en el tope de la pila.
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento del tope
     * @throws NoSuchElementException si la pila está vacía
     */
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("La pila está vacía");
        }
        return list.removeFirst();
    }

    /**
     * Retorna el elemento en el tope sin eliminarlo.
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento del tope
     * @throws NoSuchElementException si la pila está vacía
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("La pila está vacía");
        }
        return list.getFirst();
    }

    /**
     * Verifica si la pila está vacía.
     * <p>Complejidad: O(1)</p>
     *
     * @return true si la pila no contiene elementos
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Retorna el número de elementos en la pila.
     * <p>Complejidad: O(1)</p>
     *
     * @return el tamaño de la pila
     */
    public int size() {
        return list.size();
    }

    /**
     * Elimina todos los elementos de la pila.
     * <p>Complejidad: O(1)</p>
     */
    public void clear() {
        list.clear();
    }

    /**
     * Retorna un iterador sobre los elementos de la pila (desde el tope hacia la base).
     * <p>Complejidad: O(1) para creación, O(n) para recorrido completo</p>
     *
     * @return un iterador
     */
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
