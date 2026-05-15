package com.uniquindio.fintech.controller;

import com.uniquindio.fintech.model.Transaction;
import com.uniquindio.fintech.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la visualización de analíticas y estadísticas.
 * <p>Reúne todos los reportes del sistema en una sola página:
 * billeteras más usadas, usuarios más activos, categorías,
 * frecuencia por tipo, relaciones de transferencia, ciclos,
 * top de transacciones por monto y usuario más activo en período.</p>
 */
@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Crea el controlador de analíticas con inyección de dependencias.
     *
     * @param analyticsService servicio de analíticas
     */
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Muestra la página de analíticas con todos los reportes disponibles.
     *
     * @param model modelo de la vista
     * @param from  inicio del rango (opcional, por defecto 30 días atrás)
     * @param to    fin del rango (opcional, por defecto ahora)
     * @return nombre de la plantilla de analíticas
     */
    @GetMapping
    public String analytics(Model model,
                            @RequestParam(value = "from", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                            LocalDateTime from,
                            @RequestParam(value = "to", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                            LocalDateTime to) {
        LocalDateTime effectiveTo = (to != null) ? to : LocalDateTime.now();
        LocalDateTime effectiveFrom = (from != null) ? from
                : effectiveTo.minusDays(30);

        model.addAttribute("topWallets",
                analyticsService.getTopWalletsByUsage(10).toJavaList());
        model.addAttribute("topUsers",
                analyticsService.getTopUsersByTransfers(10).toJavaList());
        model.addAttribute("categories",
                hashTableToMap(analyticsService.getMostActiveCategories()));
        model.addAttribute("frequencyByType",
                hashTableToMap(analyticsService.getTransactionFrequencyByType()));
        model.addAttribute("transferRelationships",
                analyticsService.getTransferRelationships().toJavaList());
        model.addAttribute("hasCycles",
                analyticsService.detectCyclesInTransfers());

        List<Transaction> topTx =
                analyticsService.getTopTransactionsByAmount(10).toJavaList();
        model.addAttribute("topTransactions", topTx);

        String mostActive = analyticsService.getMostActiveUserInPeriod(
                effectiveFrom, effectiveTo);
        model.addAttribute("mostActiveUser", mostActive);
        model.addAttribute("periodFrom", effectiveFrom);
        model.addAttribute("periodTo", effectiveTo);

        double total = analyticsService.getTotalAmountByDateRange(
                effectiveFrom, effectiveTo);
        model.addAttribute("totalInPeriod", total);

        return "analytics/index";
    }

    /**
     * Convierte una HashTable personalizada a un Map de Java estándar.
     *
     * @param hashTable tabla hash personalizada
     * @return mapa de Java con las mismas entradas
     */
    private Map<String, Integer> hashTableToMap(
            com.uniquindio.fintech.datastructures.hash.HashTable<String, Integer> hashTable) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String key : hashTable.keys()) {
            map.put(key, hashTable.get(key));
        }
        return map;
    }
}
