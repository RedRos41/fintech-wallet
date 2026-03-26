package com.uniquindio.fintech.model;

import com.uniquindio.fintech.model.enums.TransactionStatus;
import com.uniquindio.fintech.model.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa una transacción financiera dentro del sistema de billeteras.
 * <p>Cada transacción tiene un identificador único generado automáticamente,
 * un tipo, un monto, billeteras de origen y destino, y un estado.</p>
 */
public class Transaction {

    private final String id;
    private final LocalDateTime date;
    private final TransactionType type;
    private final double amount;
    private final String sourceWalletCode;
    private final String targetWalletCode;
    private TransactionStatus status;
    private int pointsGenerated;
    private double commissionCharged;
    private final String description;

    /**
     * Crea una nueva transacción con identificador UUID generado automáticamente.
     *
     * @param date             fecha y hora de la transacción
     * @param type             tipo de transacción
     * @param amount           monto de la transacción
     * @param sourceWalletCode código de la billetera de origen
     * @param targetWalletCode código de la billetera de destino
     * @param status           estado de la transacción
     * @param pointsGenerated  puntos generados por esta transacción
     * @param commissionCharged comisión cobrada
     * @param description      descripción de la transacción
     */
    public Transaction(LocalDateTime date, TransactionType type, double amount,
                       String sourceWalletCode, String targetWalletCode,
                       TransactionStatus status, int pointsGenerated,
                       double commissionCharged, String description) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.sourceWalletCode = sourceWalletCode;
        this.targetWalletCode = targetWalletCode;
        this.status = status;
        this.pointsGenerated = pointsGenerated;
        this.commissionCharged = commissionCharged;
        this.description = description;
    }

    /**
     * Retorna el identificador único de la transacción.
     *
     * @return id de la transacción
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna la fecha y hora de la transacción.
     *
     * @return fecha de la transacción
     */
    public LocalDateTime getDate() {
        return date;
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
     * Retorna el monto de la transacción.
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
     * Retorna el estado actual de la transacción.
     *
     * @return estado de la transacción
     */
    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * Establece el estado de la transacción.
     *
     * @param status nuevo estado
     */
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    /**
     * Retorna los puntos generados por esta transacción.
     *
     * @return puntos generados
     */
    public int getPointsGenerated() {
        return pointsGenerated;
    }

    /**
     * Establece los puntos generados por esta transacción.
     *
     * @param pointsGenerated puntos generados
     */
    public void setPointsGenerated(int pointsGenerated) {
        this.pointsGenerated = pointsGenerated;
    }

    /**
     * Retorna la comisión cobrada en esta transacción.
     *
     * @return comisión cobrada
     */
    public double getCommissionCharged() {
        return commissionCharged;
    }

    /**
     * Establece la comisión cobrada en esta transacción.
     *
     * @param commissionCharged comisión cobrada
     */
    public void setCommissionCharged(double commissionCharged) {
        this.commissionCharged = commissionCharged;
    }

    /**
     * Retorna la descripción de la transacción.
     *
     * @return descripción
     */
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", type=" + type +
                ", amount=" + amount +
                ", sourceWalletCode='" + sourceWalletCode + '\'' +
                ", targetWalletCode='" + targetWalletCode + '\'' +
                ", status=" + status +
                ", pointsGenerated=" + pointsGenerated +
                ", commissionCharged=" + commissionCharged +
                ", description='" + description + '\'' +
                '}';
    }
}
