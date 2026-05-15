package com.uniquindio.fintech.service;

import com.uniquindio.fintech.datastructures.hash.HashTable;
import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import com.uniquindio.fintech.datastructures.queue.PriorityQueue;
import com.uniquindio.fintech.datastructures.tree.BinarySearchTree;
import com.uniquindio.fintech.model.UserPointsEntry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que compara el rendimiento de distintas estructuras de datos
 * implementadas en el proyecto sobre operaciones equivalentes.
 * <p>Cumple el requisito 8 del PDF: "comparar el rendimiento de
 * distintas estructuras en operaciones similares".</p>
 */
@Service
public class StructureBenchmarkService {

    /**
     * Resultado de un escenario de benchmark.
     */
    public static class BenchmarkResult {
        private final String scenario;
        private final String structureA;
        private final String structureB;
        private final long timeA;
        private final long timeB;
        private final String winner;

        public BenchmarkResult(String scenario, String structureA, String structureB,
                               long timeA, long timeB) {
            this.scenario = scenario;
            this.structureA = structureA;
            this.structureB = structureB;
            this.timeA = timeA;
            this.timeB = timeB;
            this.winner = timeA <= timeB ? structureA : structureB;
        }

        public String getScenario() { return scenario; }
        public String getStructureA() { return structureA; }
        public String getStructureB() { return structureB; }
        public long getTimeA() { return timeA; }
        public long getTimeB() { return timeB; }
        public String getWinner() { return winner; }
        public double getSpeedup() {
            long max = Math.max(timeA, timeB);
            long min = Math.max(1, Math.min(timeA, timeB));
            return (double) max / min;
        }
    }

    /**
     * Ejecuta los escenarios estándar de comparación con el tamaño dado.
     *
     * @param n cantidad de elementos a usar en cada escenario
     * @return lista de resultados, uno por escenario
     */
    public List<BenchmarkResult> runAll(int n) {
        List<BenchmarkResult> results = new ArrayList<>();
        results.add(searchByKey(n));
        results.add(orderedRetrieval(n));
        results.add(topElement(n));
        return results;
    }

    /**
     * Compara la búsqueda por clave entre HashTable y SimpleLinkedList.
     * <p>HashTable tiene búsqueda O(1) amortizado; la lista es O(n).</p>
     *
     * @param n cantidad de elementos
     * @return resultado del escenario
     */
    public BenchmarkResult searchByKey(int n) {
        HashTable<String, Integer> hash = new HashTable<>();
        SimpleLinkedList<String> list = new SimpleLinkedList<>();
        for (int i = 0; i < n; i++) {
            String key = "K" + i;
            hash.put(key, i);
            list.addLast(key);
        }
        String target = "K" + (n - 1);

        long t0 = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            hash.get(target);
        }
        long timeHash = System.nanoTime() - t0;

        t0 = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            for (String s : list) {
                if (s.equals(target)) {
                    break;
                }
            }
        }
        long timeList = System.nanoTime() - t0;

        return new BenchmarkResult(
                "Búsqueda por clave (1000 lookups)",
                "HashTable", "SimpleLinkedList",
                timeHash, timeList);
    }

    /**
     * Compara la obtención de elementos en orden ascendente entre
     * BinarySearchTree (recorrido in-order) y SimpleLinkedList (ordenamiento manual).
     *
     * @param n cantidad de elementos
     * @return resultado del escenario
     */
    public BenchmarkResult orderedRetrieval(int n) {
        BinarySearchTree<UserPointsEntry> bst = new BinarySearchTree<>();
        SimpleLinkedList<UserPointsEntry> list = new SimpleLinkedList<>();
        for (int i = 0; i < n; i++) {
            int pts = (int) (Math.random() * 10000);
            bst.insert(new UserPointsEntry("U" + i, pts));
            list.addLast(new UserPointsEntry("U" + i, pts));
        }

        long t0 = System.nanoTime();
        SimpleLinkedList<UserPointsEntry> ordered = bst.inOrder();
        for (UserPointsEntry e : ordered) {
            e.getPoints();
        }
        long timeBST = System.nanoTime() - t0;

        t0 = System.nanoTime();
        SimpleLinkedList<UserPointsEntry> sorted = insertionSort(list);
        for (UserPointsEntry e : sorted) {
            e.getPoints();
        }
        long timeList = System.nanoTime() - t0;

        return new BenchmarkResult(
                "Obtención en orden ascendente",
                "BinarySearchTree", "SimpleLinkedList",
                timeBST, timeList);
    }

    /**
     * Compara la extracción del elemento de mayor prioridad entre
     * PriorityQueue y SimpleLinkedList con búsqueda lineal del mínimo.
     *
     * @param n cantidad de elementos
     * @return resultado del escenario
     */
    public BenchmarkResult topElement(int n) {
        PriorityQueue<UserPointsEntry> pq = new PriorityQueue<>();
        SimpleLinkedList<UserPointsEntry> list = new SimpleLinkedList<>();
        for (int i = 0; i < n; i++) {
            int pts = (int) (Math.random() * 10000);
            pq.enqueue(new UserPointsEntry("U" + i, pts));
            list.addLast(new UserPointsEntry("U" + i, pts));
        }

        long t0 = System.nanoTime();
        UserPointsEntry topPq = pq.peek();
        topPq.getPoints();
        long timePq = System.nanoTime() - t0;

        t0 = System.nanoTime();
        UserPointsEntry best = null;
        for (UserPointsEntry e : list) {
            if (best == null || e.compareTo(best) < 0) {
                best = e;
            }
        }
        if (best != null) best.getPoints();
        long timeList = System.nanoTime() - t0;

        return new BenchmarkResult(
                "Obtener elemento de mayor prioridad",
                "PriorityQueue", "SimpleLinkedList",
                timePq, timeList);
    }

    /**
     * Insertion sort sobre una SimpleLinkedList. Se usa solo para que la
     * comparación con BST sea justa: ambas estructuras producen una lista
     * ordenada al final.
     */
    private SimpleLinkedList<UserPointsEntry> insertionSort(
            SimpleLinkedList<UserPointsEntry> source) {
        SimpleLinkedList<UserPointsEntry> sorted = new SimpleLinkedList<>();
        for (UserPointsEntry e : source) {
            int idx = 0;
            boolean inserted = false;
            for (UserPointsEntry s : sorted) {
                if (e.compareTo(s) < 0) {
                    sorted.add(idx, e);
                    inserted = true;
                    break;
                }
                idx++;
            }
            if (!inserted) {
                sorted.addLast(e);
            }
        }
        return sorted;
    }
}
