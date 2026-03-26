package com.uniquindio.fintech.controller;

import com.uniquindio.fintech.model.ScheduledOperation;
import com.uniquindio.fintech.model.enums.TransactionType;
import com.uniquindio.fintech.service.ScheduledOperationService;
import com.uniquindio.fintech.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Controlador para la gestión de operaciones programadas.
 * <p>Permite listar operaciones pendientes, programar nuevas operaciones
 * y procesar todas las operaciones vencidas.</p>
 */
@Controller
@RequestMapping("/scheduled")
public class ScheduledController {

    private final ScheduledOperationService scheduledOperationService;
    private final UserService userService;

    /**
     * Crea el controlador de operaciones programadas con inyección de dependencias.
     *
     * @param scheduledOperationService servicio de operaciones programadas
     * @param userService               servicio de usuarios
     */
    public ScheduledController(
            ScheduledOperationService scheduledOperationService,
            UserService userService) {
        this.scheduledOperationService = scheduledOperationService;
        this.userService = userService;
    }

    /**
     * Muestra la lista de operaciones programadas pendientes.
     *
     * @param model modelo de la vista
     * @return nombre de la plantilla de lista de operaciones programadas
     */
    @GetMapping
    public String listScheduled(Model model) {
        model.addAttribute("operations",
                scheduledOperationService.getPendingOperations().toJavaList());
        return "scheduled/list";
    }

    /**
     * Muestra el formulario para programar una nueva operación.
     *
     * @param model modelo de la vista
     * @return nombre de la plantilla del formulario de operación programada
     */
    @GetMapping("/new")
    public String newScheduledForm(Model model) {
        model.addAttribute("users", userService.getAllUsers().toJavaList());
        model.addAttribute("transactionTypes", TransactionType.values());
        return "scheduled/form";
    }

    /**
     * Programa una nueva operación para ejecución futura.
     *
     * @param sourceWalletCode   código de billetera origen
     * @param targetWalletCode   código de billetera destino (opcional)
     * @param type               tipo de transacción
     * @param amount             monto de la operación
     * @param scheduledDate      fecha y hora programada (formato ISO)
     * @param description        descripción de la operación
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de operaciones programadas
     */
    @PostMapping("/create")
    public String createScheduled(
            @RequestParam String sourceWalletCode,
            @RequestParam(required = false) String targetWalletCode,
            @RequestParam TransactionType type,
            @RequestParam double amount,
            @RequestParam String scheduledDate,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(scheduledDate);
            ScheduledOperation operation = new ScheduledOperation(
                    dateTime, type, amount,
                    sourceWalletCode, targetWalletCode, description);
            scheduledOperationService.scheduleOperation(operation);
            redirectAttributes.addFlashAttribute("success",
                    "Operation scheduled successfully");
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Invalid date format: " + scheduledDate);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/scheduled";
    }

    /**
     * Procesa todas las operaciones programadas cuya fecha ha vencido.
     *
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de operaciones programadas
     */
    @PostMapping("/process")
    public String processAllDue(RedirectAttributes redirectAttributes) {
        try {
            int count = scheduledOperationService.processAllDue(
                    LocalDateTime.now());
            redirectAttributes.addFlashAttribute("success",
                    count + " scheduled operation(s) processed successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/scheduled";
    }
}
