package com.lab04.citas.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    private static Authentication autenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return auth;
    }

    /** Username del usuario autenticado; "sistema" si no hay sesión (p. ej. el inicializador de datos). */
    public static String usuarioActual() {
        Authentication auth = autenticacion();
        return auth == null ? "sistema" : auth.getName();
    }

    /** Código del rol del usuario autenticado (sin prefijo ROLE_); vacío si no hay sesión. */
    public static String rolActual() {
        Authentication auth = autenticacion();
        if (auth == null) return "";
        for (GrantedAuthority a : auth.getAuthorities()) {
            String nombre = a.getAuthority();
            return nombre.startsWith("ROLE_") ? nombre.substring(5) : nombre;
        }
        return "";
    }
}
