package org.example.ufc_api.controller;

import org.example.ufc_api.dto.UsuarioDto;
import org.example.ufc_api.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UsuarioDto dto) {
        try {
            logger.info("🆕 Creando usuario: {}", dto.getCorreo());

            UsuarioDto createdUser = service.create(dto);

            // Respuesta con información útil
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente. Se ha enviado un email de bienvenida.");
            response.put("usuario", Map.of(
                    "id", createdUser.getId(),
                    "nombre", createdUser.getNombre(),
                    "correo", createdUser.getCorreo(),
                    "rol", createdUser.getRol(),
                    "fechaCreacion", createdUser.getFechaCreacion()
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            logger.error("❌ Error creando usuario: {}", e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logger.error("❌ Error interno creando usuario: {}", e.getMessage(), e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<UsuarioDto>> getAll() {
        try {
            List<UsuarioDto> usuarios = service.findAll();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            logger.error("❌ Error obteniendo usuarios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            UsuarioDto usuario = service.findById(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("❌ Error obteniendo usuario {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UsuarioDto dto) {
        try {
            UsuarioDto updatedUser = service.update(id, dto);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario actualizado exitosamente");
            response.put("usuario", updatedUser);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("❌ Error actualizando usuario {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario eliminado exitosamente");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("❌ Error eliminando usuario {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/rol")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id, @RequestParam String rol) {
        try {
            UsuarioDto updatedUser = service.changeRole(id, rol);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rol actualizado exitosamente");
            response.put("usuario", updatedUser);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            logger.error("❌ Error cambiando rol del usuario {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}