package com.lab04.citas.service;

import com.lab04.citas.dto.UsuarioFormDTO;
import com.lab04.citas.entity.Rol;
import com.lab04.citas.entity.Usuario;
import com.lab04.citas.exception.NegocioException;
import com.lab04.citas.repository.RolRepository;
import com.lab04.citas.repository.UsuarioRepository;
import com.lab04.citas.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private static final int CLAVE_MINIMA = 6;

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                          PasswordEncoder passwordEncoder, AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new NegocioException("Usuario no encontrado"));
    }

    public Usuario registrar(UsuarioFormDTO dto) {
        String username = dto.getUsername().trim();
        if (usuarioRepository.existsByUsername(username)) {
            throw new NegocioException("El usuario '" + username + "' ya existe");
        }
        validarClave(dto.getPassword(), true);
        Rol rol = rolActivo(dto.getRolId());

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setNombres(dto.getNombres().trim());
        u.setApellidos(dto.getApellidos().trim());
        u.setRol(rol);
        u.setActivo(true);
        u = usuarioRepository.save(u);
        auditoriaService.registrar("REGISTRO", "Usuario", u.getId(),
                "Usuario " + u.getUsername() + " con rol " + rol.getNombre());
        return u;
    }

    // El username no se modifica (es la identidad usada en la auditoría).
    public Usuario actualizar(Long id, UsuarioFormDTO dto) {
        Usuario u = buscarPorId(id);
        boolean cambiaRol = !u.getRol().getId().equals(dto.getRolId());

        if (cambiaRol) {
            if (u.getUsername().equals(SecurityUtils.usuarioActual())) {
                throw new NegocioException("No puedes cambiar tu propio rol: podrías perder el acceso.");
            }
            u.setRol(rolActivo(dto.getRolId()));
        }
        u.setNombres(dto.getNombres().trim());
        u.setApellidos(dto.getApellidos().trim());

        boolean cambiaClave = dto.getPassword() != null && !dto.getPassword().isBlank();
        if (cambiaClave) {
            validarClave(dto.getPassword(), false);
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        u = usuarioRepository.save(u);
        auditoriaService.registrar("MODIFICACION", "Usuario", u.getId(),
                "Usuario " + u.getUsername() + (cambiaRol ? " | nuevo rol " + u.getRol().getNombre() : "")
                        + (cambiaClave ? " | contraseña actualizada" : ""));
        return u;
    }

    public void cambiarEstado(Long id) {
        Usuario u = buscarPorId(id);
        if (u.isActivo() && u.getUsername().equals(SecurityUtils.usuarioActual())) {
            throw new NegocioException("No puedes desactivar tu propio usuario.");
        }
        u.setActivo(!u.isActivo());
        usuarioRepository.save(u);
        auditoriaService.registrar(u.isActivo() ? "ACTIVACION" : "DESACTIVACION", "Usuario", u.getId(),
                "Usuario " + u.getUsername());
    }

    /** Roles que se pueden elegir en el formulario: los activos, más el actual del usuario que se edita. */
    @Transactional(readOnly = true)
    public List<Rol> rolesDisponibles(Rol actual) {
        List<Rol> roles = new java.util.ArrayList<>(rolRepository.findByActivoTrueOrderByNombre());
        if (actual != null && roles.stream().noneMatch(r -> r.getId().equals(actual.getId()))) {
            roles.add(actual);
        }
        return roles;
    }

    private Rol rolActivo(Long rolId) {
        Rol rol = rolRepository.findById(rolId).orElseThrow(() -> new NegocioException("Rol no encontrado"));
        if (!rol.isActivo()) {
            throw new NegocioException("El rol " + rol.getNombre() + " está desactivado");
        }
        return rol;
    }

    private void validarClave(String clave, boolean obligatoria) {
        if (clave == null || clave.isBlank()) {
            if (obligatoria) throw new NegocioException("La contraseña es obligatoria");
            return;
        }
        if (clave.length() < CLAVE_MINIMA) {
            throw new NegocioException("La contraseña debe tener al menos " + CLAVE_MINIMA + " caracteres");
        }
    }
}
