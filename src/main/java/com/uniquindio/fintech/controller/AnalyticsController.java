package com.uniquindio.fintech.controller;

import com.uniquindio.fintech.service.AnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controlador para la visualización de analíticas y estadísticas.
 * <p>Reúne todos los reportes del sistema en una sola página:
 * billeteras más usadas, usuarios más activos, categorías,
 * frecuencia por tipo, relaciones de transferencia y ciclos.</p>
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
     * @return nombre de la plantilla de analíticas
     */
    @GetMapping
    public String analytics(Model model) {
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
