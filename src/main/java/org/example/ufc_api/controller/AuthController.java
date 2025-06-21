package org.example.ufc_api.controller;

import org.example.ufc_api.dto.UsuarioDto;
import org.example.ufc_api.model.Usuario;
import org.example.ufc_api.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            logger.info("🔐 Intento de login para: {}", email);

            // Buscar usuario por email
            Usuario usuario = usuarioRepository.findByCorreo(email)
                    .orElse(null);

            if (usuario == null) {
                logger.warn("❌ Usuario no encontrado: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Credenciales inválidas"));
            }

            // Verificar contraseña
            if (!passwordEncoder.matches(password, usuario.getPassword())) {
                logger.warn("❌ Contraseña incorrecta para: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Credenciales inválidas"));
            }

            // Login exitoso
            logger.info("✅ Login exitoso para: {} con rol: {}", email, usuario.getRol());

            Map<String, Object> response = new HashMap<>();
            response.put("id", usuario.getId());
            response.put("nombre", usuario.getNombre());
            response.put("correo", usuario.getCorreo());
            response.put("rol", usuario.getRol());
            response.put("fechaCreacion", usuario.getFechaCreacion());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error en login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAuth(@RequestHeader("Authorization") String authHeader) {
        try {
            // Este endpoint verifica si las credenciales Basic Auth son válidas
            // Spring Security ya habrá validado las credenciales si llegamos aquí

            // Extraer credenciales del header Basic Auth
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
            String email = credentials.split(":")[0];

            Usuario usuario = usuarioRepository.findByCorreo(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Map<String, Object> response = new HashMap<>();
            response.put("id", usuario.getId());
            response.put("nombre", usuario.getNombre());
            response.put("correo", usuario.getCorreo());
            response.put("rol", usuario.getRol());
            response.put("authenticated", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error verificando autenticación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }
    }
}