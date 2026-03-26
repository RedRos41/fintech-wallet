package com.uniquindio.fintech.service;

import com.uniquindio.fintech.datastructures.graph.Graph;
import com.uniquindio.fintech.datastructures.hash.HashTable;
import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.Wallet;
import com.uniquindio.fintech.model.enums.TransactionStatus;
import com.uniquindio.fintech.model.enums.TransactionType;
import com.uniquindio.fintech.model.enums.WalletType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio encargado de proporcionar analíticas y estadísticas del sistema.
 * <p>Ofrece consultas sobre billeteras más usadas, usuarios más activos,
 * categorías más frecuentes, montos por rango de fechas, frecuencia
 * por tipo de transacción, relaciones de transferencia y detección de ciclos.</p>
 */
@Service
public class AnalyticsService {

    private final DataStore dataStore;

    /**
     * Crea el servicio de analíticas con inyección de dependencias.
     *
     * @param dataStore almacén central de datos
     */
    public AnalyticsService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Retorna las billeteras con mayor cantidad de transacciones.
     * <p>Recorre todas las billeteras, cuenta sus transacciones
     * y retorna las primeras según el límite indicado.</p>
     *
     * @param limit cantidad máxima de billeteras a retornar
     * @return lista de códigos de billetera ordenados por uso descendente
     */
    public SimpleLinkedList<String> getTopWalletsByUsage(int limit) {
        SimpleLinkedList<Wallet> wallets = dataStore.getWalletsByCode().values();
        HashTable<String, Integer> counts = new HashTable<>();
        for (Wallet w : wallets) {
            counts.put(w.getCode(), w.getTransactionHistory().size());
        }
        return getTopKeys(counts, limit);
    }

    /**
     * Retorna los usuarios con mayor cantidad de transferencias realizadas.
     *
     * @param limit cantidad máxima de usuarios a retornar
     * @return lista de ids de usuario ordenados por transferencias descendentes
     */
    public SimpleLinkedList<String> getTopUsersByTransfers(int limit) {
        SimpleLinkedList<User> users = dataStore.getUsersById().values();
        HashTable<String, Integer> counts = new HashTable<>();
        for (User u : users) {
            int count = countByType(u, TransactionType.TRANSFER);
            counts.put(u.getId(), count);
        }
        return getTopKeys(counts, limit);
    }

    /**
     * Cuenta las transacciones agrupadas por tipo de billetera (categoría).
     *
     * @return tabla hash con la cantidad de transacciones por tipo de billetera
     */
    public HashTable<String, Integer> getMostActiveCategories() {
        HashTable<String, Integer> categories = new HashTable<>();
        for (WalletType wt : WalletType.values()) {
            categories.put(wt.name(), 0);
        }
        SimpleLinkedList<Wallet> wallets = dataStore.getWalletsByCode().values();
        for (Wallet w : wallets) {
            String key = w.getType().name();
            int current = categories.get(key);
            categories.put(key, current + w.getTransactionHistory().size());
        }
        return categories;
    }

    /**
     * Suma los montos de todas las transacciones completadas en un rango de fechas.
     *
     * @param from inicio del rango (inclusivo)
     * @param to   fin del rango (inclusivo)
     * @return el monto total acumulado en el rango
     */
    public double getTotalAmountByDateRange(LocalDateTime from,
                                            LocalDateTime to) {
        double total = 0;
        SimpleLinkedList<User> users = dataStore.getUsersById().values();
        HashTable<String, Boolean> counted = new HashTable<>();
        for (User u : users) {
            for (Transaction tx : u.getTransactionHistory()) {
                if (counted.containsKey(tx.getId())) {
                    continue;
                }
                if (tx.getStatus() == TransactionStatus.COMPLETED
                        && !tx.getDate().isBefore(from)
                        && !tx.getDate().isAfter(to)) {
                    total += tx.getAmount();
                    counted.put(tx.getId(), true);
                }
            }
        }
        return total;
    }

    /**
     * Cuenta las transacciones completadas agrupadas por tipo de transacción.
     *
     * @return tabla hash con la cantidad de transacciones por tipo
     */
    public HashTable<String, Integer> getTransactionFrequencyByType() {
        HashTable<String, Integer> freq = new HashTable<>();
        for (TransactionType tt : TransactionType.values()) {
            freq.put(tt.name(), 0);
        }
        HashTable<String, Boolean> counted = new HashTable<>();
        SimpleLinkedList<User> users = dataStore.getUsersById().values();
        for (User u : users) {
            for (Transaction tx : u.getTransactionHistory()) {
                if (counted.containsKey(tx.getId())) {
                    continue;
                }
                if (tx.getStatus() == TransactionStatus.COMPLETED) {
                    String key = tx.getType().name();
                    freq.put(key, freq.get(key) + 1);
                    counted.put(tx.getId(), true);
                }
            }
        }
        return freq;
    }

    /**
     * Retorna las relaciones (aristas) del grafo de transferencias.
     * <p>Cada entrada en la lista resultante tiene el formato "origen->destino".</p>
     *
     * @return lista de cadenas representando las aristas del grafo
     */
    public SimpleLinkedList<String> getTransferRelationships() {
        SimpleLinkedList<String> edges = new SimpleLinkedList<>();
        Graph<String> graph = dataStore.getTransferGraph();
        SimpleLinkedList<String> vertices = graph.getVertices();
        for (String from : vertices) {
            SimpleLinkedList<String> neighbors = graph.getNeighbors(from);
            for (String to : neighbors) {
                edges.addLast(from + "->" + to);
            }
        }
        return edges;
    }

    /**
     * Detecta si existen ciclos en el grafo de transferencias.
     * <p>Delega al método {@code hasCycles()} del grafo.</p>
     *
     * @return true si se detectan ciclos en el grafo de transferencias
     */
    public boolean detectCyclesInTransfers() {
        return dataStore.getTransferGraph().hasCycles();
    }

    /**
     * Cuenta transacciones completadas de un tipo específico para un usuario.
     *
     * @param user usuario a evaluar
     * @param type tipo de transacción a contar
     * @return cantidad de transacciones del tipo indicado
     */
    private int countByType(User user, TransactionType type) {
        int count = 0;
        for (Transaction tx : user.getTransactionHistory()) {
            if (tx.getType() == type
                    && tx.getStatus() == TransactionStatus.COMPLETED) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retorna las top N claves de una tabla hash ordenadas por valor descendente.
     *
     * @param counts tabla hash de clave a conteo
     * @param limit  cantidad máxima de resultados
     * @return lista con las claves de mayor conteo
     */
    private SimpleLinkedList<String> getTopKeys(
            HashTable<String, Integer> counts, int limit) {
        SimpleLinkedList<String> result = new SimpleLinkedList<>();
        SimpleLinkedList<String> keys = counts.keys();
        for (int i = 0; i < limit; i++) {
            String bestKey = null;
            int bestVal = -1;
            for (String key : keys) {
                int val = counts.get(key);
                if (val > bestVal) {
                    bestVal = val;
                    bestKey = key;
                }
            }
            if (bestKey == null) {
                break;
            }
            result.addLast(bestKey);
            counts.put(bestKey, -1);
        }
        return result;
    }
}
