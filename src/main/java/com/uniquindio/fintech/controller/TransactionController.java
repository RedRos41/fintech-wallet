package com.uniquindio.fintech.controller;

import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.model.User;
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
     * Ejecuta una transacción según el tipo seleccionado.
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
            switch (TransactionType.valueOf(type)) {
                case DEPOSIT:
                    transactionService.depositToWallet(
                            sourceWalletCode, amount);
                    break;
                case WITHDRAWAL:
                    transactionService.withdrawFromWallet(
                            sourceWalletCode, amount);
                    break;
                case TRANSFER:
                    transactionService.transferBetweenUsers(
                            sourceWalletCode, targetWalletCode, amount);
                    break;
                default:
                    break;
            }
            redirectAttributes.addFlashAttribute("success",
                    "Transaction executed successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transactions";
    }

    /**
     * Revierte la última operación de un usuario.
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
                    "Transaction " + reversed.getId() + " reversed successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transactions";
    }

    /**
     * Recolecta todas las transacciones de todos los usuarios sin duplicados.
     *
     * @return lista de transacciones únicas
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
        return all;
    }
}
