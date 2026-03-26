package com.uniquindio.fintech.controller;

import com.uniquindio.fintech.model.User;
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
 * Controlador para la gestión de usuarios.
 * <p>Permite listar, crear, ver detalle, editar y eliminar usuarios
 * del sistema de billeteras fintech.</p>
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * Crea el controlador de usuarios con inyección de dependencias.
     *
     * @param userService servicio de usuarios
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Muestra la lista de todos los usuarios registrados.
     *
     * @param model modelo de la vista
     * @return nombre de la plantilla de lista de usuarios
     */
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers().toJavaList());
        return "users/list";
    }

    /**
     * Muestra el formulario para crear un nuevo usuario.
     *
     * @return nombre de la plantilla del formulario de usuario
     */
    @GetMapping("/new")
    public String newUserForm() {
        return "users/form";
    }

    /**
     * Crea un nuevo usuario con los datos del formulario.
     *
     * @param id                 cédula del usuario
     * @param name               nombre completo
     * @param email              correo electrónico
     * @param phone              número de teléfono
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de usuarios
     */
    @PostMapping("/create")
    public String createUser(@RequestParam String id,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String phone,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(id, name, email, phone);
            redirectAttributes.addFlashAttribute("success",
                    "User created successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    /**
     * Muestra el detalle de un usuario con sus billeteras, puntos y nivel.
     *
     * @param id    cédula del usuario
     * @param model modelo de la vista
     * @param redirectAttributes atributos flash para mensajes de error
     * @return nombre de la plantilla de detalle de usuario
     */
    @GetMapping("/{id}")
    public String userDetail(@PathVariable String id, Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("wallets", user.getWallets().toJavaList());
            model.addAttribute("benefits",
                    user.getRedeemedBenefits().toJavaList());
            return "users/detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users";
        }
    }

    /**
     * Muestra el formulario de edición de un usuario existente.
     *
     * @param id    cédula del usuario
     * @param model modelo de la vista
     * @param redirectAttributes atributos flash para mensajes de error
     * @return nombre de la plantilla del formulario de usuario
     */
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable String id, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("user", userService.findUserById(id));
            return "users/form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users";
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param id                 cédula del usuario
     * @param name               nuevo nombre
     * @param email              nuevo correo electrónico
     * @param phone              nuevo teléfono
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al detalle del usuario
     */
    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable String id,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String phone,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, name, email, phone);
            redirectAttributes.addFlashAttribute("success",
                    "User updated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users/" + id;
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param id                 cédula del usuario a eliminar
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de usuarios
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable String id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success",
                    "User deleted successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }
}
