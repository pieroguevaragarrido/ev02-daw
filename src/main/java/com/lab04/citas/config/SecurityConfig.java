package com.lab04.citas.config;

import com.lab04.citas.security.LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginSuccessHandler successHandler) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/error").permitAll()
                // Solo ADMINISTRADOR: usuarios, roles y auditoría
                .requestMatchers("/usuarios/**", "/roles/**", "/auditoria/**").hasRole("ADMINISTRADOR")
                // Citas y médicos: ADMINISTRADOR y RECEPCIONISTA
                .requestMatchers("/citas/**", "/medicos/**").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                // Pacientes: los tres roles
                .requestMatchers("/pacientes/**").hasAnyRole("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA")
                // Cualquier otra ruta exige haber iniciado sesión
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"));
        return http.build();
    }
}
