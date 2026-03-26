package com.uniquindio.fintech.controller;

import com.uniquindio.fintech.model.AuditEvent;
import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.enums.RiskLevel;
import com.uniquindio.fintech.service.FraudDetectionService;
import com.uniquindio.fintech.service.UserService;
import com.uniquindio.fintech.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador principal del panel de control (dashboard).
 * <p>Muestra un resumen general del sistema incluyendo conteos,
 * transacciones recientes y alertas activas de fraude.</p>
 */
@Controller
@RequestMapping("/")
public class DashboardController {

    private final UserService userService;
    private final FraudDetectionService fraudDetectionService;

    /**
     * Crea el controlador del dashboard con inyección de dependencias.
     *
     * @param userService           servicio de usuarios
     * @param fraudDetectionService servicio de detección de fraudes
     */
    public DashboardController(UserService userService,
                               FraudDetectionService fraudDetectionService) {
        this.userService = userService;
        this.fraudDetectionService = fraudDetectionService;
    }

    /**
     * Muestra la página principal del dashboard con estadísticas generales.
     *
     * @param model modelo de la vista
     * @return nombre de la plantilla dashboard
     */
    @GetMapping
    public String dashboard(Model model) {
        List<User> users = userService.getAllUsers().toJavaList();
        model.addAttribute("totalUsers", users.size());
        int totalWallets = users.stream()
                .mapToInt(u -> u.getWallets().size()).sum();
        model.addAttribute("totalWallets", totalWallets);
        List<Transaction> allTx = collectAllTransactions(users);
        model.addAttribute("totalTransactions", allTx.size());
        List<Transaction> last10 = allTx.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .limit(10).collect(Collectors.toList());
        model.addAttribute("recentTransactions", last10);
        List<AuditEvent> alerts = getActiveAlerts();
        model.addAttribute("alerts", alerts);
        return "dashboard";
    }

    /**
     * Recolecta todas las transacciones de todos los usuarios sin duplicados.
     *
     * @param users lista de usuarios
     * @return lista de transacciones únicas
     */
    private List<Transaction> collectAllTransactions(List<User> users) {
        List<Transaction> all = new ArrayList<>();
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (User u : users) {
            for (Transaction tx : u.getTransactionHistory()) {
                if (seen.add(tx.getId())) {
                    all.add(tx);
                }
            }
        }
        return all;
    }

    /**
     * Obtiene los eventos de auditoría con riesgo HIGH o CRITICAL.
     *
     * @return lista de alertas activas
     */
    private List<AuditEvent> getActiveAlerts() {
        List<AuditEvent> alerts = new ArrayList<>();
        for (AuditEvent event : fraudDetectionService.getAuditEvents()) {
            if (event.getRiskLevel() == RiskLevel.HIGH
                    || event.getRiskLevel() == RiskLevel.CRITICAL) {
                alerts.add(event);
            }
        }
        return alerts;
    }
}
