package com.uniquindio.fintech.service;

import com.uniquindio.fintech.model.Notification;
import com.uniquindio.fintech.model.enums.NotificationType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio encargado de gestionar las notificaciones del sistema.
 * <p>Permite enviar notificaciones a los usuarios y encolarlas
 * en la cola de notificaciones pendientes del DataStore.</p>
 */
@Service
public class NotificationService {

    private final DataStore dataStore;

    /**
     * Crea el servicio de notificaciones con inyección del almacén de datos.
     *
     * @param dataStore almacén central de datos
     */
    public NotificationService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Envía una notificación a un usuario específico.
     * <p>Crea la notificación, la agrega a las notificaciones del usuario
     * y la encola en las notificaciones pendientes del sistema.</p>
     *
     * @param userId  cédula del usuario destinatario
     * @param type    tipo de notificación
     * @param message mensaje de la notificación
     */
    public void sendNotification(String userId, NotificationType type,
                                 String message) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador del usuario no puede ser nulo o vacío");
        }
        if (!dataStore.getUsersById().containsKey(userId)) {
            throw new IllegalArgumentException(
                    "No existe un usuario con el identificador: " + userId);
        }
        Notification notification = new Notification(
                LocalDateTime.now(), type, message, userId);
        dataStore.getUsersById().get(userId)
                .getNotifications().addLast(notification);
        dataStore.getPendingNotifications().enqueue(notification);
    }
}
