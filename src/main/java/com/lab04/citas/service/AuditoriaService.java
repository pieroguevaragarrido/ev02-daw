package com.lab04.citas.service;

import com.lab04.citas.entity.Auditoria;
import com.lab04.citas.repository.AuditoriaRepository;
import com.lab04.citas.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    /**
     * Registra una operación. Se une a la transacción del servicio que la llama:
     * si la operación de negocio falla y hace rollback, el registro de auditoría también.
     */
    @Transactional
    public void registrar(String operacion, String entidad, Long registroId, String detalle) {
        Auditoria a = new Auditoria();
        a.setUsuario(SecurityUtils.usuarioActual());
        a.setFechaHora(LocalDateTime.now());
        a.setOperacion(operacion);
        a.setEntidad(entidad);
        a.setRegistroId(registroId);
        if (detalle != null && detalle.length() > 255) {
            detalle = detalle.substring(0, 255);
        }
        a.setDetalle(detalle);
        auditoriaRepository.save(a);
    }

    @Transactional(readOnly = true)
    public List<Auditoria> buscar(String entidad, String usuario, String operacion) {
        return auditoriaRepository.buscar(entidad, usuario, operacion);
    }
}
