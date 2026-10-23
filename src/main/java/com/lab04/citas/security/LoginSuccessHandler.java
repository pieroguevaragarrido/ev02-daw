package com.lab04.citas.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Redirige a cada usuario a su pantalla principal según su rol. */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String destino = "/inicio";
        for (GrantedAuthority a : authentication.getAuthorities()) {
            switch (a.getAuthority()) {
                case "ROLE_ADMINISTRADOR" -> destino = "/usuarios";
                case "ROLE_MEDICO" -> destino = "/pacientes";
                case "ROLE_RECEPCIONISTA" -> destino = "/citas";
                default -> { }
            }
        }
        response.sendRedirect(request.getContextPath() + destino);
    }
}
