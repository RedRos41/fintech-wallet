package com.uniquindio.fintech.controller;

import com.uniquindio.fintech.model.Notification;
import com.uniquindio.fintech.service.DataStore;
import com.uniquindio.fintech.service.FraudDetectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la visualización de notificaciones y eventos de auditoría.
 * <p>Muestra todas las notificaciones pendientes del sistema junto con
 * los eventos de auditoría registrados por el servicio de detección de fraudes.</p>
 */
@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final DataStore dataStore;
    private final FraudDetectionService fraudDetectionService;

    /**
     * Crea el controlador de notificaciones con inyección de dependencias.
     *
     * @param dataStore             almacén central de datos
     * @param fraudDetectionService servicio de detección de fraudes
     */
    public NotificationController(DataStore dataStore,
                                  FraudDetectionService fraudDetectionService) {
        this.dataStore = dataStore;
        this.fraudDetectionService = fraudDetectionService;
    }

    /**
     * Muestra todas las notificaciones pendientes y los eventos de auditoría.
     *
     * @param model modelo de la vista
     * @return nombre de la plantilla de lista de notificaciones
     */
    @GetMapping
    public String listNotifications(Model model) {
        List<Notification> notifications = new ArrayList<>();
        for (Notification n : dataStore.getPendingNotifications()) {
            notifications.add(n);
        }
        model.addAttribute("notifications", notifications);
        model.addAttribute("auditEvents",
                fraudDetectionService.getAuditEvents().toJavaList());
        return "notifications/list";
    }
}
