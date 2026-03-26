package com.uniquindio.fintech.model;

import com.uniquindio.fintech.model.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa una operación programada para ejecutarse en una fecha futura.
 * <p>Implementa {@link Comparable} para ordenarse por fecha programada,
 * lo que permite su uso en colas de prioridad.</p>
 */
public class ScheduledOperation implements Comparable<ScheduledOperation> {

    private final String id;
    private final LocalDateTime scheduledDate;
    private final TransactionType type;
    private final double amount;
    private final String sourceWalletCode;
    private final String targetWalletCode;
    private final String description;
    private boolean executed;

    /**
     * Crea una nueva operación programada con identificador autogenerado.
     *
     * @param scheduledDate    fecha y hora programada para la ejecución
     * @param type             tipo de transacción
     * @param amount           monto de la operación
     * @param sourceWalletCode código de la billetera de origen
     * @param targetWalletCode código de la billetera de destino
     * @param description      descripción de la operación
     */
    public ScheduledOperation(LocalDateTime scheduledDate, TransactionType type,
                              double amount, String sourceWalletCode,
                              String targetWalletCode, String description) {
        this.id = UUID.randomUUID().toString();
        this.scheduledDate = scheduledDate;
        this.type = type;
        this.amount = amount;
        this.sourceWalletCode = sourceWalletCode;
        this.targetWalletCode = targetWalletCode;
        this.description = description;
        this.executed = false;
    }

    /**
     * Retorna el identificador único de la operación programada.
     *
     * @return id de la operación
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna la fecha y hora programada para la ejecución.
     *
     * @return fecha programada
     */
    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    /**
     * Retorna el tipo de transacción.
     *
     * @return tipo de transacción
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Retorna el monto de la operación.
     *
     * @return monto
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Retorna el código de la billetera de origen.
     *
     * @return código de billetera origen
     */
    public String getSourceWalletCode() {
        return sourceWalletCode;
    }

    /**
     * Retorna el código de la billetera de destino.
     *
     * @return código de billetera destino
     */
    public String getTargetWalletCode() {
        return targetWalletCode;
    }

    /**
     * Retorna la descripción de la operación.
     *
     * @return descripción
     */
    public String getDescription() {
        return description;
    }

    /**
     * Indica si la operación ya fue ejecutada.
     *
     * @return true si fue ejecutada
     */
    public boolean isExecuted() {
        return executed;
    }

    /**
     * Establece el estado de ejecución de la operación.
     *
     * @param executed true si fue ejecutada
     */
    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    /**
     * Compara esta operación con otra por fecha programada.
     * <p>Las operaciones con fecha más temprana tienen mayor prioridad (menor valor).</p>
     *
     * @param other la otra operación a comparar
     * @return valor negativo, cero o positivo según el orden natural
     */
    @Override
    public int compareTo(ScheduledOperation other) {
        return this.scheduledDate.compareTo(other.scheduledDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledOperation that = (ScheduledOperation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ScheduledOperation{" +
                "id='" + id + '\'' +
                ", scheduledDate=" + scheduledDate +
                ", type=" + type +
                ", amount=" + amount +
                ", sourceWalletCode='" + sourceWalletCode + '\'' +
                ", targetWalletCode='" + targetWalletCode + '\'' +
                ", description='" + description + '\'' +
                ", executed=" + executed +
                '}';
    }
}
