package com.uniquindio.fintech.service;

import com.uniquindio.fintech.datastructures.hash.HashTable;
import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import com.uniquindio.fintech.model.AuditEvent;
import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.enums.RiskLevel;
import com.uniquindio.fintech.model.enums.TransactionStatus;
import com.uniquindio.fintech.model.enums.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio encargado de la detección de fraudes en transacciones.
 * <p>Aplica un conjunto de reglas heurísticas sobre las transacciones
 * de los usuarios y genera eventos de auditoría cuando se detectan
 * patrones sospechosos.</p>
 */
@Service
public class FraudDetectionService {

    private final DataStore dataStore;

    /**
     * Crea el servicio de detección de fraudes con inyección de dependencias.
     *
     * @param dataStore almacén central de datos
     */
    public FraudDetectionService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Analiza una transacción para un usuario aplicando las reglas de fraude.
     * <p>Reglas aplicadas:
     * <ol>
     *   <li>Más de 5 transacciones en el último minuto: riesgo ALTO</li>
     *   <li>Monto mayor a 3 veces el promedio del usuario: riesgo MEDIO</li>
     *   <li>Más de 3 transacciones al mismo destino en 5 minutos: riesgo ALTO</li>
     *   <li>Transferencias a más de 3 billeteras distintas en 10 minutos: riesgo MEDIO</li>
     * </ol></p>
     *
     * @param user        el usuario a analizar
     * @param transaction la transacción a evaluar
     */
    public void analyzeTransaction(User user, Transaction transaction) {
        if (user == null || transaction == null) {
            return;
        }
        checkHighFrequency(user, transaction);
        checkAmountAnomaly(user, transaction);
        checkRepeatedDestination(user, transaction);
        checkFragmentation(user, transaction);
    }

    /**
     * Analiza las transacciones recientes de todos los usuarios del sistema.
     * <p>Para cada usuario, evalúa su última transacción completada
     * contra las reglas de detección de fraude.</p>
     */
    public void analyzeAllUsers() {
        SimpleLinkedList<User> users = dataStore.getUsersById().values();
        for (User user : users) {
            if (user.getTransactionHistory().isEmpty()) {
                continue;
            }
            Transaction last = user.getTransactionHistory().getLast();
            if (last.getStatus() == TransactionStatus.COMPLETED) {
                analyzeTransaction(user, last);
            }
        }
    }

    /**
     * Retorna el registro completo de eventos de auditoría.
     *
     * @return lista enlazada simple de eventos de auditoría
     */
    public SimpleLinkedList<AuditEvent> getAuditEvents() {
        return dataStore.getAuditLog();
    }

    /**
     * Regla 1: más de 5 transacciones completadas en el último minuto.
     *
     * @param user usuario a evaluar
     * @param tx   transacción actual
     */
    private void checkHighFrequency(User user, Transaction tx) {
        LocalDateTime oneMinuteAgo = tx.getDate().minusMinutes(1);
        int count = 0;
        for (Transaction t : user.getTransactionHistory()) {
            if (t.getStatus() == TransactionStatus.COMPLETED
                    && !t.getDate().isBefore(oneMinuteAgo)) {
                count++;
            }
        }
        if (count > 5) {
            addAuditEvent(user, tx, RiskLevel.HIGH,
                    "Alta frecuencia de transacciones",
                    count + " transacciones en el último minuto");
        }
    }

    /**
     * Regla 2: monto mayor a 3 veces el promedio del usuario.
     *
     * @param user usuario a evaluar
     * @param tx   transacción actual
     */
    private void checkAmountAnomaly(User user, Transaction tx) {
        double average = calculateAverage(user);
        if (average > 0 && tx.getAmount() > 3 * average) {
            addAuditEvent(user, tx, RiskLevel.MEDIUM,
                    "Monto anómalo detectado",
                    "Monto " + tx.getAmount()
                    + " supera 3x el promedio " + average);
        }
    }

    /**
     * Regla 3: más de 3 transacciones al mismo destino en 5 minutos.
     *
     * @param user usuario a evaluar
     * @param tx   transacción actual
     */
    private void checkRepeatedDestination(User user, Transaction tx) {
        if (tx.getTargetWalletCode() == null) {
            return;
        }
        LocalDateTime fiveMinAgo = tx.getDate().minusMinutes(5);
        int count = 0;
        for (Transaction t : user.getTransactionHistory()) {
            if (t.getStatus() == TransactionStatus.COMPLETED
                    && !t.getDate().isBefore(fiveMinAgo)
                    && tx.getTargetWalletCode()
                        .equals(t.getTargetWalletCode())) {
                count++;
            }
        }
        if (count > 3) {
            addAuditEvent(user, tx, RiskLevel.HIGH,
                    "Destino repetido sospechoso",
                    count + " transacciones al destino "
                    + tx.getTargetWalletCode() + " en 5 minutos");
        }
    }

    /**
     * Regla 4: transferencias a más de 3 billeteras distintas en 10 minutos.
     *
     * @param user usuario a evaluar
     * @param tx   transacción actual
     */
    private void checkFragmentation(User user, Transaction tx) {
        LocalDateTime tenMinAgo = tx.getDate().minusMinutes(10);
        HashTable<String, Boolean> destinations = new HashTable<>();
        for (Transaction t : user.getTransactionHistory()) {
            if (t.getType() == TransactionType.TRANSFER
                    && t.getStatus() == TransactionStatus.COMPLETED
                    && !t.getDate().isBefore(tenMinAgo)
                    && t.getTargetWalletCode() != null) {
                destinations.put(t.getTargetWalletCode(), true);
            }
        }
        if (destinations.size() > 3) {
            addAuditEvent(user, tx, RiskLevel.MEDIUM,
                    "Fragmentación de transferencias",
                    destinations.size()
                    + " destinos distintos en 10 minutos");
        }
    }

    /**
     * Calcula el monto promedio de las transacciones completadas de un usuario.
     *
     * @param user el usuario
     * @return el monto promedio, o 0.0 si no tiene transacciones
     */
    private double calculateAverage(User user) {
        double sum = 0;
        int count = 0;
        for (Transaction t : user.getTransactionHistory()) {
            if (t.getStatus() == TransactionStatus.COMPLETED) {
                sum += t.getAmount();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    /**
     * Registra un evento de auditoría en el registro del sistema.
     *
     * @param user        usuario involucrado
     * @param tx          transacción sospechosa
     * @param riskLevel   nivel de riesgo
     * @param description descripción del evento
     * @param details     detalles adicionales
     */
    private void addAuditEvent(User user, Transaction tx,
                               RiskLevel riskLevel,
                               String description, String details) {
        AuditEvent event = new AuditEvent(
                LocalDateTime.now(), user.getId(), tx.getId(),
                riskLevel, description, details);
        dataStore.getAuditLog().addLast(event);
    }
}
