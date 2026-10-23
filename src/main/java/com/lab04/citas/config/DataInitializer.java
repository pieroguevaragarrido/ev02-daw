package com.lab04.citas.config;

import com.lab04.citas.entity.Rol;
import com.lab04.citas.entity.Usuario;
import com.lab04.citas.repository.RolRepository;
import com.lab04.citas.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Crea los roles base y un usuario de prueba por rol si todavía no existen. */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Rol admin = crearRol("ADMINISTRADOR", "Administrador");
        Rol medico = crearRol("MEDICO", "Médico");
        Rol recep = crearRol("RECEPCIONISTA", "Recepcionista");

        crearUsuario("admin", "admin123", "Administrador", "del Sistema", admin);
        crearUsuario("medico", "medico123", "Carlos", "Ramírez López", medico);
        crearUsuario("recepcion", "recep123", "Lucía", "Fernández Soto", recep);
    }

    private Rol crearRol(String nombre, String descripcion) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> {
            Rol r = new Rol();
            r.setNombre(nombre);
            r.setDescripcion(descripcion);
            r.setActivo(true);
            return rolRepository.save(r);
        });
    }

    private void crearUsuario(String username, String clave, String nombres, String apellidos, Rol rol) {
        if (usuarioRepository.existsByUsername(username)) return;
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(clave));
        u.setNombres(nombres);
        u.setApellidos(apellidos);
        u.setRol(rol);
        u.setActivo(true);
        usuarioRepository.save(u);
    }
}
