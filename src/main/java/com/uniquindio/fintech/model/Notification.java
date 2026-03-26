package com.uniquindio.fintech.model;

import com.uniquindio.fintech.model.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa una notificación enviada a un usuario del sistema.
 * <p>Las notificaciones informan sobre eventos relevantes como saldo bajo,
 * operaciones programadas, rechazos, subidas de nivel, entre otros.</p>
 */
public class Notification {

    private final String id;
    private final LocalDateTime date;
    private final NotificationType type;
    private final String message;
    private boolean read;
    private final String userId;

    /**
     * Crea una nueva notificación con identificador autogenerado.
     *
     * @param date    fecha y hora de la notificación
     * @param type    tipo de notificación
     * @param message mensaje de la notificación
     * @param userId  cédula del usuario destinatario
     */
    public Notification(LocalDateTime date, NotificationType type,
                        String message, String userId) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.type = type;
        this.message = message;
        this.read = false;
        this.userId = userId;
    }

    /**
     * Retorna el identificador único de la notificación.
     *
     * @return id de la notificación
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna la fecha y hora de la notificación.
     *
     * @return fecha de la notificación
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Retorna el tipo de notificación.
     *
     * @return tipo de notificación
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Retorna el mensaje de la notificación.
     *
     * @return mensaje
     */
    public String getMessage() {
        return message;
    }

    /**
     * Indica si la notificación fue leída.
     *
     * @return true si fue leída
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Establece el estado de lectura de la notificación.
     *
     * @param read true si fue leída
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Retorna la cédula del usuario destinatario.
     *
     * @return id del usuario
     */
    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", type=" + type +
                ", message='" + message + '\'' +
                ", read=" + read +
                ", userId='" + userId + '\'' +
                '}';
    }
}
