package com.lab04.citas.controller;

import com.lab04.citas.service.AuditoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String entidad,
                         @RequestParam(required = false) String usuario,
                         @RequestParam(required = false) String operacion,
                         Model model) {
        // Un campo de formulario vacío llega como "", no como null: se normaliza
        entidad = vacioANulo(entidad);
        usuario = vacioANulo(usuario);
        operacion = vacioANulo(operacion);

        model.addAttribute("registros", auditoriaService.buscar(entidad, usuario, operacion));
        model.addAttribute("entidades", List.of("Cita", "Usuario", "Rol"));
        model.addAttribute("operaciones", List.of("REGISTRO", "MODIFICACION", "CAMBIO_ESTADO",
                "CANCELACION", "ACTIVACION", "DESACTIVACION"));
        model.addAttribute("fEntidad", entidad);
        model.addAttribute("fUsuario", usuario);
        model.addAttribute("fOperacion", operacion);
        return "auditoria/lista";
    }

    private String vacioANulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
