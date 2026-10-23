package com.lab04.citas.repository;

import com.lab04.citas.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<Rol> findByActivoTrueOrderByNombre();
}
