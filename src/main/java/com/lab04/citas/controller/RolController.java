package com.lab04.citas.controller;

import com.lab04.citas.dto.RolFormDTO;
import com.lab04.citas.entity.Rol;
import com.lab04.citas.exception.NegocioException;
import com.lab04.citas.service.RolService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("roles", rolService.listar());
        return "roles/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("rolForm", new RolFormDTO());
        return "roles/formulario";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("rolForm") RolFormDTO form,
                            BindingResult result, Model model, RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                rolService.registrar(form);
                flash.addFlashAttribute("mensaje", "Rol registrado correctamente");
                return "redirect:/roles";
            } catch (NegocioException ex) {
                model.addAttribute("errorNegocio", ex.getMessage());
            }
        }
        return "roles/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Rol r = rolService.buscarPorId(id);
        RolFormDTO dto = new RolFormDTO();
        dto.setNombre(r.getNombre());
        dto.setDescripcion(r.getDescripcion());
        model.addAttribute("rolForm", dto);
        model.addAttribute("rolId", id);
        return "roles/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("rolForm") RolFormDTO form,
                             BindingResult result, Model model, RedirectAttributes flash) {
        // El código no se edita: se restablece el valor real para que el formulario no lo pierda
        form.setNombre(rolService.buscarPorId(id).getNombre());
        if (!result.hasErrors()) {
            try {
                rolService.actualizar(id, form);
                flash.addFlashAttribute("mensaje", "Rol actualizado correctamente");
                return "redirect:/roles";
            } catch (NegocioException ex) {
                model.addAttribute("errorNegocio", ex.getMessage());
            }
        }
        model.addAttribute("rolId", id);
        return "roles/formulario";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes flash) {
        try {
            rolService.cambiarEstado(id);
            flash.addFlashAttribute("mensaje", "Estado del rol actualizado");
        } catch (NegocioException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/roles";
    }
}
