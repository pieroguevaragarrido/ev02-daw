package com.lab04.citas.controller;

import com.lab04.citas.entity.Paciente;
import com.lab04.citas.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteRepository pacienteRepository;

    public PacienteController(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pacientes", pacienteRepository.findAll());
        return "pacientes/lista";
    }

    // Detalle: muestra las citas del paciente a través de la relación OneToMany
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Paciente p = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado"));
        model.addAttribute("paciente", p);
        return "pacientes/detalle";
    }
}
