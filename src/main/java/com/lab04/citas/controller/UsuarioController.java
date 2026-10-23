package com.lab04.citas.controller;

import com.lab04.citas.dto.UsuarioFormDTO;
import com.lab04.citas.entity.Usuario;
import com.lab04.citas.exception.NegocioException;
import com.lab04.citas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuarioForm", new UsuarioFormDTO());
        model.addAttribute("roles", usuarioService.rolesDisponibles(null));
        return "usuarios/formulario";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("usuarioForm") UsuarioFormDTO form,
                            BindingResult result, Model model, RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                usuarioService.registrar(form);
                flash.addFlashAttribute("mensaje", "Usuario registrado correctamente");
                return "redirect:/usuarios";
            } catch (NegocioException ex) {
                model.addAttribute("errorNegocio", ex.getMessage());
            }
        }
        model.addAttribute("roles", usuarioService.rolesDisponibles(null));
        return "usuarios/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Usuario u = usuarioService.buscarPorId(id);
        UsuarioFormDTO dto = new UsuarioFormDTO();
        dto.setUsername(u.getUsername());
        dto.setNombres(u.getNombres());
        dto.setApellidos(u.getApellidos());
        dto.setRolId(u.getRol().getId());
        model.addAttribute("usuarioForm", dto);
        model.addAttribute("usuarioId", id);
        model.addAttribute("roles", usuarioService.rolesDisponibles(u.getRol()));
        return "usuarios/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("usuarioForm") UsuarioFormDTO form,
                             BindingResult result, Model model, RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                usuarioService.actualizar(id, form);
                flash.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
                return "redirect:/usuarios";
            } catch (NegocioException ex) {
                model.addAttribute("errorNegocio", ex.getMessage());
            }
        }
        model.addAttribute("usuarioId", id);
        model.addAttribute("roles", usuarioService.rolesDisponibles(usuarioService.buscarPorId(id).getRol()));
        return "usuarios/formulario";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes flash) {
        try {
            usuarioService.cambiarEstado(id);
            flash.addFlashAttribute("mensaje", "Estado del usuario actualizado");
        } catch (NegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }
}
