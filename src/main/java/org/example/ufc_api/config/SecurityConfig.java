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
                        .roles(u.getRol())   // "ADMIN", "USER", etc.
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET,    "/api/usuarios").hasRole("admin")

                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("usuario", "admin")
                        .requestMatchers("/live-fight/**").permitAll()
                        .requestMatchers(HttpMethod.POST,   "/api/**").hasRole("admin")
                        .requestMatchers(HttpMethod.PUT,    "/api/**").hasRole("admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("admin")
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/assets/**").permitAll()
                        .anyRequest().authenticated()

                )
                .httpBasic(Customizer.withDefaults());   // o JWT/FBEARER
        return http.build();
    }


}
