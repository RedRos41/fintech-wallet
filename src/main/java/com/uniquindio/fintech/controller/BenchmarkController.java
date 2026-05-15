package com.uniquindio.fintech.controller;

import com.uniquindio.fintech.service.StructureBenchmarkService;
import com.uniquindio.fintech.service.StructureBenchmarkService.BenchmarkResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controlador para la página de comparación de rendimiento entre
 * estructuras de datos. Cumple el requisito 8 del PDF.
 */
@Controller
@RequestMapping("/benchmark")
public class BenchmarkController {

    private final StructureBenchmarkService benchmarkService;

    /**
     * Crea el controlador con inyección de dependencias.
     *
     * @param benchmarkService servicio de benchmark
     */
    public BenchmarkController(StructureBenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    /**
     * Ejecuta los benchmarks y muestra los resultados.
     *
     * @param n     tamaño de la muestra (por defecto 5000)
     * @param model modelo de la vista
     * @return nombre de la plantilla
     */
    @GetMapping
    public String run(@RequestParam(value = "n", defaultValue = "5000") int n,
                      Model model) {
        int safeN = Math.max(100, Math.min(n, 50000));
        List<BenchmarkResult> results = benchmarkService.runAll(safeN);
        model.addAttribute("results", results);
        model.addAttribute("n", safeN);
        return "benchmark/index";
    }
}
