package com.uniquindio.fintech.controller;

import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.enums.TransactionStatus;
import com.uniquindio.fintech.model.enums.TransactionType;
import com.uniquindio.fintech.service.ReversalService;
import com.uniquindio.fintech.service.TransactionService;
import com.uniquindio.fintech.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión de transacciones financieras.
 * <p>Permite listar transacciones con filtros, ejecutar depósitos,
 * retiros y transferencias, y revertir operaciones.</p>
 */
@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final ReversalService reversalService;
    private final UserService userService;

    /**
     * Crea el controlador de transacciones con inyección de dependencias.
     *
     * @param transactionService servicio de transacciones
     * @param reversalService    servicio de reversión
     * @param userService        servicio de usuarios
     */
    public TransactionController(TransactionService transactionService,
                                 ReversalService reversalService,
                                 UserService userService) {
        this.transactionService = transactionService;
        this.reversalService = reversalService;
        this.userService = userService;
    }

    /**
     * Muestra la lista de todas las transacciones con filtros opcionales.
     *
     * @param typeFilter   filtro por tipo de transacción (opcional)
     * @param statusFilter filtro por estado de transacción (opcional)
     * @param model        modelo de la vista
     * @return nombre de la plantilla de lista de transacciones
     */
    @GetMapping
    public String listTransactions(
            @RequestParam(required = false) String typeFilter,
            @RequestParam(required = false) String statusFilter,
            Model model) {
        List<Transaction> all = collectAllTransactions();
        List<Transaction> filtered = all.stream()
                .filter(tx -> typeFilter == null || typeFilter.isEmpty()
                        || tx.getType().name().equals(typeFilter))
                .filter(tx -> statusFilter == null || statusFilter.isEmpty()
                        || tx.getStatus().name().equals(statusFilter))
                .collect(Collectors.toList());
        model.addAttribute("transactions", filtered);
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("typeFilter", typeFilter);
        model.addAttribute("statusFilter", statusFilter);
        return "transactions/list";
    }

    /**
     * Muestra el formulario para crear una nueva transacción.
     *
     * @param model modelo de la vista
     * @return nombre de la plantilla del formulario de transacciones
     */
    @GetMapping("/new")
    public String newTransactionForm(Model model) {
        model.addAttribute("users", userService.getAllUsers().toJavaList());
        model.addAttribute("transactionTypes", TransactionType.values());
        return "transactions/form";
    }

    /**
     * Ejecuta una transacción según el tipo seleccionado y emite un mensaje
     * flash específico según el tipo (depósito/retiro/transferencia) y el
     * estado resultante (COMPLETED/REJECTED).
     *
     * @param type               tipo de transacción
     * @param sourceWalletCode   código de billetera origen
     * @param targetWalletCode   código de billetera destino (para transferencias)
     * @param amount             monto de la transacción
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de transacciones
     */
    @PostMapping("/execute")
    public String executeTransaction(
            @RequestParam String type,
            @RequestParam String sourceWalletCode,
            @RequestParam(required = false) String targetWalletCode,
            @RequestParam double amount,
            RedirectAttributes redirectAttributes) {
        try {
            TransactionType txType = TransactionType.valueOf(type);
            Transaction tx;
            switch (txType) {
                case DEPOSIT:
                    tx = transactionService.depositToWallet(
                            sourceWalletCode, amount);
                    break;
                case WITHDRAWAL:
                    tx = transactionService.withdrawFromWallet(
                            sourceWalletCode, amount);
                    break;
                case TRANSFER:
                    tx = transactionService.transferBetweenUsers(
                            sourceWalletCode, targetWalletCode, amount);
                    break;
                default:
                    redirectAttributes.addFlashAttribute("error",
                            "Tipo de transacción no soportado: " + type);
                    return "redirect:/transactions";
            }
            applyTransactionFlash(redirectAttributes, txType, tx, amount);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transactions";
    }

    /**
     * Revierte la última operación de un usuario y emite un mensaje flash
     * específico según el tipo de operación revertida.
     *
     * @param userId             cédula del usuario
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de transacciones
     */
    @PostMapping("/reverse/{userId}")
    public String reverseLastOperation(
            @PathVariable String userId,
            RedirectAttributes redirectAttributes) {
        try {
            Transaction reversed = reversalService.reverseLastOperation(userId);
            redirectAttributes.addFlashAttribute("success",
                    buildReversalMessage(userId, reversed));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transactions";
    }

    /**
     * Construye el mensaje específico para una reversión según el tipo
     * de operación revertida, indicando los efectos concretos (devolución
     * de saldos, reembolso de comisión y reversión de puntos).
     */
    private String buildReversalMessage(String userId, Transaction reversed) {
        String formatted = String.format("$%,.2f", reversed.getAmount());
        int points = reversed.getPointsGenerated();
        switch (reversed.getType()) {
            case DEPOSIT:
                return "Depósito de " + formatted + " revertido. Se descontó "
                        + formatted + " de la billetera. Puntos restados: -"
                        + points + ".";
            case WITHDRAWAL:
                return "Retiro de " + formatted + " revertido. Se reintegró "
                        + formatted + " a la billetera. Puntos restados: -"
                        + points + ".";
            case TRANSFER:
                String commission = String.format("$%,.2f",
                        reversed.getCommissionCharged());
                return "Transferencia de " + formatted + " revertida. "
                        + "Origen recibe " + formatted + " (incluye comisión "
                        + commission + "). Destino devuelve el monto recibido. "
                        + "Puntos restados al remitente: -" + points + ".";
            case SCHEDULED_PAYMENT:
                return "Pago programado de " + formatted
                        + " revertido. Saldos y puntos del usuario " + userId
                        + " actualizados.";
            default:
                return "Operación de " + formatted + " del usuario " + userId
                        + " revertida exitosamente.";
        }
    }

    /**
     * Construye el flash message apropiado segun el tipo y el estado de la transacción.
     */
    private void applyTransactionFlash(RedirectAttributes ra,
                                       TransactionType txType,
                                       Transaction tx,
                                       double amount) {
        String formatted = String.format("$%,.2f", amount);
        if (tx.getStatus() == TransactionStatus.COMPLETED) {
            ra.addFlashAttribute("success",
                    buildSuccessMessage(txType, tx, formatted));
        } else if (tx.getStatus() == TransactionStatus.REJECTED) {
            ra.addFlashAttribute("warning",
                    buildRejectionMessage(txType, formatted, tx.getDescription()));
        } else {
            ra.addFlashAttribute("success",
                    labelFor(txType) + " registrada con estado "
                    + tx.getStatus() + ".");
        }
    }

    private String buildSuccessMessage(TransactionType txType,
                                       Transaction tx,
                                       String formatted) {
        switch (txType) {
            case DEPOSIT:
                return "Depósito de " + formatted
                        + " acreditado en la billetera. Puntos ganados: +"
                        + tx.getPointsGenerated() + ".";
            case WITHDRAWAL:
                return "Retiro de " + formatted
                        + " ejecutado correctamente. Puntos ganados: +"
                        + tx.getPointsGenerated() + ".";
            case TRANSFER:
                String commission = String.format("$%,.2f",
                        tx.getCommissionCharged());
                return "Transferencia de " + formatted
                        + " completada. Comisión: " + commission
                        + ". Puntos ganados: +"
                        + tx.getPointsGenerated() + ".";
            default:
                return "Transacción completada.";
        }
    }

    private String buildRejectionMessage(TransactionType txType,
                                         String formatted,
                                         String reason) {
        String motivo = (reason == null || reason.isBlank())
                ? "no se pudo procesar la operación"
                : reason;
        switch (txType) {
            case DEPOSIT:
                return "Depósito de " + formatted + " RECHAZADO. " + motivo;
            case WITHDRAWAL:
                return "Retiro de " + formatted + " RECHAZADO. " + motivo;
            case TRANSFER:
                return "Transferencia de " + formatted + " RECHAZADA. " + motivo;
            default:
                return "Transacción rechazada. " + motivo;
        }
    }

    private String labelFor(TransactionType type) {
        switch (type) {
            case DEPOSIT: return "Depósito";
            case WITHDRAWAL: return "Retiro";
            case TRANSFER: return "Transferencia";
            case SCHEDULED_PAYMENT: return "Pago programado";
            default: return "Operación";
        }
    }

    /**
     * Recolecta todas las transacciones de todos los usuarios sin duplicados,
     * ordenadas por fecha descendente (la más reciente primero).
     *
     * @return lista de transacciones únicas, más recientes arriba
     */
    private List<Transaction> collectAllTransactions() {
        List<Transaction> all = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (User u : userService.getAllUsers()) {
            for (Transaction tx : u.getTransactionHistory()) {
                if (seen.add(tx.getId())) {
                    all.add(tx);
                }
            }
        }
        all.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return all;
    }
}
