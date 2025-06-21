package org.example.ufc_api.config;

import org.example.ufc_api.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository repo){
        return username -> repo.findByCorreo(username)
                .map(u -> User.builder()
                        .username(u.getCorreo())
                        .password(u.getPassword())
                        .roles(u.getRol())   // "admin", "usuario", etc.
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ✅ ARCHIVOS ESTÁTICOS - SIN AUTENTICACIÓN
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/assets/**", "/favicon.ico").permitAll()

                        // ✅ WEBSOCKET Y SOCKJS - SIN AUTENTICACIÓN
                        .requestMatchers("/live-fight/**", "/sockjs-node/**", "/info/**").permitAll()

                        // ✅ REGISTRO DE USUARIOS - SIN AUTENTICACIÓN
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                        // ✅ ENDPOINT DE AUTENTICACIÓN - PERMITE LOGIN DE CUALQUIER ROL
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/usuarios/profile").authenticated() // Usuario puede ver su perfil

                        // ❌ ENDPOINTS API CON AUTENTICACIÓN Y ROLES
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("admin") // Solo admin ve todos los usuarios
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/{id}").hasAnyRole("usuario", "admin") // Usuario puede ver info específica
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("usuario", "admin")
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("admin")

                        // ❌ TODO LO DEMÁS REQUIERE AUTENTICACIÓN
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());   // Autenticación HTTP Basic
        return http.build();
    }
}