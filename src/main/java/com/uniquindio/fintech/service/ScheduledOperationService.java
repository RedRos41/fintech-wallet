package com.uniquindio.fintech.service;

import com.uniquindio.fintech.datastructures.list.SimpleLinkedList;
import com.uniquindio.fintech.model.ScheduledOperation;
import com.uniquindio.fintech.model.enums.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio encargado de gestionar las operaciones programadas.
 * <p>Permite programar operaciones futuras, procesarlas cuando llega
 * su fecha y consultar las operaciones pendientes.</p>
 */
@Service
public class ScheduledOperationService {

    private final DataStore dataStore;
    private final TransactionService transactionService;

    /**
     * Crea el servicio de operaciones programadas con inyección de dependencias.
     *
     * @param dataStore          almacén central de datos
     * @param transactionService servicio de transacciones
     */
    public ScheduledOperationService(DataStore dataStore,
                                     TransactionService transactionService) {
        this.dataStore = dataStore;
        this.transactionService = transactionService;
    }

    /**
     * Programa una nueva operación para ejecutarse en una fecha futura.
     * <p>Valida los datos de la operación y la encola en la cola de prioridad
     * ordenada por fecha programada.</p>
     *
     * @param operation la operación programada a registrar
     * @return la operación programada registrada
     * @throws IllegalArgumentException si la operación es nula o tiene datos inválidos
     */
    public ScheduledOperation scheduleOperation(
            ScheduledOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException(
                    "La operación programada no puede ser nula");
        }
        validateScheduleInput(operation.getType(),
                operation.getAmount(), operation.getScheduledDate());
        dataStore.getScheduledOps().enqueue(operation);
        return operation;
    }

    /**
     * Procesa todas las operaciones programadas cuya fecha sea menor o igual a la actual.
     * <p>Desencola las operaciones vencidas, ejecuta cada una según su tipo
     * (depósito, retiro o transferencia) y la marca como ejecutada.</p>
     *
     * @param now fecha y hora actual de referencia
     * @return cantidad de operaciones procesadas
     */
    public int processAllDue(LocalDateTime now) {
        int processed = 0;
        while (!dataStore.getScheduledOps().isEmpty()) {
            ScheduledOperation op = dataStore.getScheduledOps().peek();
            if (op.getScheduledDate().isAfter(now)) {
                break;
            }
            dataStore.getScheduledOps().dequeue();
            executeOperation(op);
            op.setExecuted(true);
            processed++;
        }
        return processed;
    }

    /**
     * Retorna las operaciones pendientes (no ejecutadas) de la cola de prioridad.
     * <p>Itera sobre la cola sin modificarla y recolecta las que no han sido ejecutadas.</p>
     *
     * @return lista enlazada simple de operaciones pendientes
     */
    public SimpleLinkedList<ScheduledOperation> getPendingOperations() {
        SimpleLinkedList<ScheduledOperation> pending = new SimpleLinkedList<>();
        for (ScheduledOperation op : dataStore.getScheduledOps()) {
            if (!op.isExecuted()) {
                pending.addLast(op);
            }
        }
        return pending;
    }

    /**
     * Ejecuta una operación programada delegando al servicio de transacciones.
     *
     * @param op la operación a ejecutar
     */
    private void executeOperation(ScheduledOperation op) {
        switch (op.getType()) {
            case DEPOSIT:
                transactionService.depositToWallet(
                        op.getSourceWalletCode(), op.getAmount());
                break;
            case WITHDRAWAL:
                transactionService.withdrawFromWallet(
                        op.getSourceWalletCode(), op.getAmount());
                break;
            case TRANSFER:
                transactionService.transferBetweenUsers(
                        op.getSourceWalletCode(),
                        op.getTargetWalletCode(), op.getAmount());
                break;
            default:
                break;
        }
    }

    /**
     * Valida los parámetros de entrada para programar una operación.
     *
     * @param type          tipo de transacción
     * @param amount        monto
     * @param scheduledDate fecha programada
     */
    private void validateScheduleInput(TransactionType type,
                                       double amount,
                                       LocalDateTime scheduledDate) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "El tipo de transacción no puede ser nulo");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor a cero: " + amount);
        }
        if (scheduledDate == null) {
            throw new IllegalArgumentException(
                    "La fecha programada no puede ser nula");
        }
    }
}
