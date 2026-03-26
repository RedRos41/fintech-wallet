package com.uniquindio.fintech.model;

import com.uniquindio.fintech.model.enums.RiskLevel;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa un evento de auditoría registrado en el sistema.
 * <p>Los eventos de auditoría capturan acciones relevantes del sistema
 * junto con su nivel de riesgo asociado para análisis de seguridad.</p>
 */
public class AuditEvent {

    private final String id;
    private final LocalDateTime date;
    private final String userId;
    private final String transactionId;
    private final RiskLevel riskLevel;
    private final String description;
    private final String details;

    /**
     * Crea un nuevo evento de auditoría con identificador autogenerado.
     *
     * @param date          fecha y hora del evento
     * @param userId        cédula del usuario involucrado
     * @param transactionId identificador de la transacción asociada
     * @param riskLevel     nivel de riesgo del evento
     * @param description   descripción breve del evento
     * @param details       detalles adicionales del evento
     */
    public AuditEvent(LocalDateTime date, String userId,
                      String transactionId, RiskLevel riskLevel,
                      String description, String details) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.userId = userId;
        this.transactionId = transactionId;
        this.riskLevel = riskLevel;
        this.description = description;
        this.details = details;
    }

    /**
     * Retorna el identificador único del evento de auditoría.
     *
     * @return id del evento
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna la fecha y hora del evento.
     *
     * @return fecha del evento
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Retorna la cédula del usuario involucrado.
     *
     * @return id del usuario
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Retorna el identificador de la transacción asociada.
     *
     * @return id de la transacción
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Retorna el nivel de riesgo del evento.
     *
     * @return nivel de riesgo
     */
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    /**
     * Retorna la descripción breve del evento.
     *
     * @return descripción
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retorna los detalles adicionales del evento.
     *
     * @return detalles
     */
    public String getDetails() {
        return details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditEvent that = (AuditEvent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AuditEvent{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", userId='" + userId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", riskLevel=" + riskLevel +
                ", description='" + description + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
