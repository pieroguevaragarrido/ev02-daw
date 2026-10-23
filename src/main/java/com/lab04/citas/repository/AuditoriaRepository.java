package com.lab04.citas.repository;

import com.lab04.citas.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    // Filtros opcionales y combinables (mismo patrón que RF-CIT-17)
    @Query("SELECT a FROM Auditoria a WHERE " +
            "(:entidad IS NULL OR a.entidad = :entidad) AND " +
            "(:usuario IS NULL OR a.usuario = :usuario) AND " +
            "(:operacion IS NULL OR a.operacion = :operacion) " +
            "ORDER BY a.fechaHora DESC, a.id DESC")
    List<Auditoria> buscar(@Param("entidad") String entidad,
                           @Param("usuario") String usuario,
                           @Param("operacion") String operacion);
}
