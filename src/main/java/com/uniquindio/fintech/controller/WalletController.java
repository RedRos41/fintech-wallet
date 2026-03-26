package com.uniquindio.fintech.controller;

import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.model.Wallet;
import com.uniquindio.fintech.model.enums.WalletType;
import com.uniquindio.fintech.service.UserService;
import com.uniquindio.fintech.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la gestión de billeteras virtuales.
 * <p>Permite listar todas las billeteras agrupadas por usuario,
 * crear nuevas billeteras y ver el detalle con historial de transacciones.</p>
 */
@Controller
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

    /**
     * Crea el controlador de billeteras con inyección de dependencias.
     *
     * @param walletService servicio de billeteras
     * @param userService   servicio de usuarios
     */
    public WalletController(WalletService walletService,
                            UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    /**
     * Muestra todas las billeteras del sistema agrupadas por usuario.
     *
     * @param model modelo de la vista
     * @return nombre de la plantilla de lista de billeteras
     */
    @GetMapping
    public String listWallets(Model model) {
        List<User> users = userService.getAllUsers().toJavaList();
        Map<User, List<Wallet>> walletsByUser = new LinkedHashMap<>();
        for (User user : users) {
            walletsByUser.put(user, user.getWallets().toJavaList());
        }
        model.addAttribute("walletsByUser", walletsByUser);
        return "wallets/list";
    }

    /**
     * Muestra el formulario para crear una nueva billetera.
     *
     * @param userId identificador del usuario propietario (opcional)
     * @param model  modelo de la vista
     * @return nombre de la plantilla del formulario de billetera
     */
    @GetMapping("/new")
    public String newWalletForm(
            @RequestParam(required = false) String userId, Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("users", userService.getAllUsers().toJavaList());
        model.addAttribute("walletTypes", WalletType.values());
        return "wallets/form";
    }

    /**
     * Crea una nueva billetera para un usuario.
     *
     * @param ownerId            cédula del usuario propietario
     * @param name               nombre de la billetera
     * @param type               tipo de billetera
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de billeteras
     */
    @PostMapping("/create")
    public String createWallet(@RequestParam String ownerId,
                               @RequestParam String name,
                               @RequestParam WalletType type,
                               RedirectAttributes redirectAttributes) {
        try {
            walletService.createWallet(ownerId, name, type);
            redirectAttributes.addFlashAttribute("success",
                    "Wallet created successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/wallets";
    }

    /**
     * Muestra el detalle de una billetera con su historial de transacciones.
     *
     * @param code               código de la billetera
     * @param model              modelo de la vista
     * @param redirectAttributes atributos flash para mensajes de error
     * @return nombre de la plantilla de detalle de billetera
     */
    @GetMapping("/{code}")
    public String walletDetail(@PathVariable String code, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Wallet wallet = walletService.findWalletByCode(code);
            model.addAttribute("wallet", wallet);
            model.addAttribute("transactions",
                    wallet.getTransactionHistory().toJavaList());
            return "wallets/detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/wallets";
        }
    }
}
