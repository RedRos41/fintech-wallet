package com.uniquindio.fintech.datastructures.hash;

import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;

import java.util.Objects;

/**
 * Tabla hash genérica con encadenamiento separado.
 * <p>Utiliza un arreglo de {@link SimpleLinkedList} como cubetas. Se redimensiona
 * automáticamente cuando el factor de carga supera 0.75.</p>
 *
 * @param <K> tipo de las claves
 * @param <V> tipo de los valores
 */
public class HashTable<K, V> {

    /**
     * Entrada interna que almacena un par clave-valor.
     */
    static class HashEntry<K, V> {
        K key;
        V value;

        HashEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private SimpleLinkedList<HashEntry<K, V>>[] buckets;
    private int size;

    /**
     * Crea una tabla hash vacía con capacidad inicial de 16.
     */
    @SuppressWarnings("unchecked")
    public HashTable() {
        this.buckets = new SimpleLinkedList[INITIAL_CAPACITY];
        this.size = 0;
        initBuckets(buckets);
    }

    /**
     * Inserta o actualiza un par clave-valor.
     * <p>Complejidad: O(1) amortizado</p>
     *
     * @param key   la clave
     * @param value el valor
     */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("La clave no puede ser nula");
        }
        if ((double) (size + 1) / buckets.length > LOAD_FACTOR) {
            resize();
        }
        int index = getBucketIndex(key);
        HashEntry<K, V> existing = findEntry(buckets[index], key);
        if (existing != null) {
            existing.value = value;
        } else {
            buckets[index].addLast(new HashEntry<>(key, value));
            size++;
        }
    }

    /**
     * Obtiene el valor asociado a una clave.
     * <p>Complejidad: O(1) amortizado</p>
     *
     * @param key la clave a buscar
     * @return el valor asociado
     * @throws java.util.NoSuchElementException si la clave no existe
     */
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("La clave no puede ser nula");
        }
        int index = getBucketIndex(key);
        HashEntry<K, V> entry = findEntry(buckets[index], key);
        if (entry == null) {
            throw new java.util.NoSuchElementException("La clave no existe: " + key);
        }
        return entry.value;
    }

    /**
     * Elimina la entrada con la clave dada.
     * <p>Complejidad: O(n) en la cubeta, O(1) amortizado</p>
     *
     * @param key la clave de la entrada a eliminar
     * @return el valor eliminado
     * @throws java.util.NoSuchElementException si la clave no existe
     */
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("La clave no puede ser nula");
        }
        int index = getBucketIndex(key);
        SimpleLinkedList<HashEntry<K, V>> bucket = buckets[index];
        int entryIndex = findEntryIndex(bucket, key);
        if (entryIndex < 0) {
            throw new java.util.NoSuchElementException("La clave no existe: " + key);
        }
        V value = bucket.get(entryIndex).value;
        bucket.remove(entryIndex);
        size--;
        return value;
    }

    /**
     * Verifica si existe una entrada con la clave dada.
     * <p>Complejidad: O(1) amortizado</p>
     *
     * @param key la clave a verificar
     * @return true si la clave existe
     */
    public boolean containsKey(K key) {
        if (key == null) return false;
        int index = getBucketIndex(key);
        return findEntry(buckets[index], key) != null;
    }

    /**
     * Retorna todas las claves de la tabla.
     * <p>Complejidad: O(n + m) donde m es el número de cubetas</p>
     *
     * @return lista con todas las claves
     */
    public SimpleLinkedList<K> keys() {
        SimpleLinkedList<K> result = new SimpleLinkedList<>();
        for (SimpleLinkedList<HashEntry<K, V>> bucket : buckets) {
            if (bucket != null) {
                for (HashEntry<K, V> entry : bucket) {
                    result.addLast(entry.key);
                }
            }
        }
        return result;
    }

    /**
     * Retorna todos los valores de la tabla.
     * <p>Complejidad: O(n + m) donde m es el número de cubetas</p>
     *
     * @return lista con todos los valores
     */
    public SimpleLinkedList<V> values() {
        SimpleLinkedList<V> result = new SimpleLinkedList<>();
        for (SimpleLinkedList<HashEntry<K, V>> bucket : buckets) {
            if (bucket != null) {
                for (HashEntry<K, V> entry : bucket) {
                    result.addLast(entry.value);
                }
            }
        }
        return result;
    }

    /**
     * Retorna el número de entradas en la tabla.
     * <p>Complejidad: O(1)</p>
     *
     * @return el tamaño de la tabla
     */
    public int size() {
        return size;
    }

    /**
     * Verifica si la tabla está vacía.
     * <p>Complejidad: O(1)</p>
     *
     * @return true si la tabla no contiene entradas
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Elimina todas las entradas de la tabla.
     * <p>Complejidad: O(m) donde m es el número de cubetas</p>
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        this.buckets = new SimpleLinkedList[INITIAL_CAPACITY];
        initBuckets(buckets);
        this.size = 0;
    }

    // --- Métodos auxiliares privados ---

    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode()) % buckets.length;
    }

    private HashEntry<K, V> findEntry(SimpleLinkedList<HashEntry<K, V>> bucket, K key) {
        for (HashEntry<K, V> entry : bucket) {
            if (Objects.equals(entry.key, key)) {
                return entry;
            }
        }
        return null;
    }

    private int findEntryIndex(SimpleLinkedList<HashEntry<K, V>> bucket, K key) {
        int i = 0;
        for (HashEntry<K, V> entry : bucket) {
            if (Objects.equals(entry.key, key)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        SimpleLinkedList<HashEntry<K, V>>[] oldBuckets = buckets;
        buckets = new SimpleLinkedList[oldBuckets.length * 2];
        initBuckets(buckets);
        size = 0;
        for (SimpleLinkedList<HashEntry<K, V>> bucket : oldBuckets) {
            if (bucket != null) {
                for (HashEntry<K, V> entry : bucket) {
                    put(entry.key, entry.value);
                }
            }
        }
    }

    private void initBuckets(SimpleLinkedList<HashEntry<K, V>>[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new SimpleLinkedList<>();
        }
    }
}
