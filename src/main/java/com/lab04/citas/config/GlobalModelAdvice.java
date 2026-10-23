package com.lab04.citas.config;

import com.lab04.citas.security.SecurityUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Expone usuario y rol actuales a todas las vistas, para mostrar solo las opciones permitidas. */
@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("usuarioActual")
    public String usuarioActual() {
        return SecurityUtils.usuarioActual();
    }

    @ModelAttribute("rolActual")
    public String rolActual() {
        return SecurityUtils.rolActual();
    }
}
