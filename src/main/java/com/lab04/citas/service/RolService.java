package com.lab04.citas.service;

import com.lab04.citas.dto.RolFormDTO;
import com.lab04.citas.entity.Rol;
import com.lab04.citas.exception.NegocioException;
import com.lab04.citas.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RolService {

    private final RolRepository rolRepository;
    private final AuditoriaService auditoriaService;

    public RolService(RolRepository rolRepository, AuditoriaService auditoriaService) {
        this.rolRepository = rolRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Rol> listarActivos() {
        return rolRepository.findByActivoTrueOrderByNombre();
    }

    @Transactional(readOnly = true)
    public Rol buscarPorId(Long id) {
        return rolRepository.findById(id).orElseThrow(() -> new NegocioException("Rol no encontrado"));
    }

    public Rol registrar(RolFormDTO dto) {
        String codigo = dto.getNombre().trim().toUpperCase();
        if (rolRepository.existsByNombre(codigo)) {
            throw new NegocioException("Ya existe un rol con el código " + codigo);
        }
        Rol r = new Rol();
        r.setNombre(codigo);
        r.setDescripcion(dto.getDescripcion().trim());
        r.setActivo(true);
        r = rolRepository.save(r);
        auditoriaService.registrar("REGISTRO", "Rol", r.getId(), "Rol " + r.getNombre());
        return r;
    }

    // El código (nombre) no se modifica: Spring Security lo usa para autorizar.
    public Rol actualizar(Long id, RolFormDTO dto) {
        Rol r = buscarPorId(id);
        r.setDescripcion(dto.getDescripcion().trim());
        r = rolRepository.save(r);
        auditoriaService.registrar("MODIFICACION", "Rol", r.getId(), "Descripción de " + r.getNombre());
        return r;
    }

    public void cambiarEstado(Long id) {
        Rol r = buscarPorId(id);
        if (r.isActivo() && "ADMINISTRADOR".equals(r.getNombre())) {
            throw new NegocioException("No se puede desactivar el rol ADMINISTRADOR: el sistema quedaría sin acceso.");
        }
        r.setActivo(!r.isActivo());
        rolRepository.save(r);
        auditoriaService.registrar(r.isActivo() ? "ACTIVACION" : "DESACTIVACION", "Rol", r.getId(),
                "Rol " + r.getNombre());
    }
}
