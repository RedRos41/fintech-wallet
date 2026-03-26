package com.uniquindio.fintech.controller;

import com.uniquindio.fintech.model.User;
import com.uniquindio.fintech.service.DataStore;
import com.uniquindio.fintech.service.RewardService;
import com.uniquindio.fintech.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la gestión de recompensas y puntos.
 * <p>Muestra el ranking de puntos, el catálogo de beneficios,
 * el detalle de recompensas de cada usuario y permite canjear beneficios.</p>
 */
@Controller
@RequestMapping("/rewards")
public class RewardController {

    private final RewardService rewardService;
    private final UserService userService;
    private final DataStore dataStore;

    /**
     * Crea el controlador de recompensas con inyección de dependencias.
     *
     * @param rewardService servicio de recompensas
     * @param userService   servicio de usuarios
     * @param dataStore     almacén central de datos
     */
    public RewardController(RewardService rewardService,
                            UserService userService,
                            DataStore dataStore) {
        this.rewardService = rewardService;
        this.userService = userService;
        this.dataStore = dataStore;
    }

    /**
     * Muestra el ranking de puntos y el catálogo de beneficios.
     *
     * @param model modelo de la vista
     * @return nombre de la plantilla de lista de recompensas
     */
    @GetMapping
    public String listRewards(Model model) {
        model.addAttribute("ranking",
                rewardService.getRankingInOrder().toJavaList());
        model.addAttribute("benefits",
                dataStore.getBenefitCatalog().toJavaList());
        return "rewards/list";
    }

    /**
     * Muestra el detalle de recompensas de un usuario específico.
     *
     * @param userId             cédula del usuario
     * @param model              modelo de la vista
     * @param redirectAttributes atributos flash para mensajes de error
     * @return nombre de la plantilla de detalle de recompensas
     */
    @GetMapping("/{userId}")
    public String rewardDetail(@PathVariable String userId, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("redeemedBenefits",
                    user.getRedeemedBenefits().toJavaList());
            model.addAttribute("benefits",
                    dataStore.getBenefitCatalog().toJavaList());
            return "rewards/detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/rewards";
        }
    }

    /**
     * Canjea un beneficio del catálogo para un usuario.
     *
     * @param userId             cédula del usuario
     * @param benefitId          identificador del beneficio a canjear
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al detalle de recompensas del usuario
     */
    @PostMapping("/redeem")
    public String redeemBenefit(@RequestParam String userId,
                                @RequestParam String benefitId,
                                RedirectAttributes redirectAttributes) {
        try {
            rewardService.redeemBenefit(userId, benefitId);
            redirectAttributes.addFlashAttribute("success",
                    "Benefit redeemed successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rewards/" + userId;
    }
}
