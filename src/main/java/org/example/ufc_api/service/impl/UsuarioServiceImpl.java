package org.example.ufc_api.service.impl;

import jakarta.transaction.Transactional;
import org.example.ufc_api.dto.UsuarioDto;
import org.example.ufc_api.dto.UsuarioRegistroEvent;
import org.example.ufc_api.model.Usuario;
import org.example.ufc_api.repository.UsuarioRepository;
import org.example.ufc_api.service.MessagePublisher;
import org.example.ufc_api.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired private UsuarioRepository repo;
    @Autowired private ModelMapper mapper;
    @Autowired private PasswordEncoder encoder;
    @Autowired private MessagePublisher messagePublisher; // NUEVO: Inyectar MessagePublisher

    @Override
    public UsuarioDto create(UsuarioDto dto){
        try {
            logger.info("🆕 Creando nuevo usuario: {}", dto.getCorreo());

            // Verificar si el usuario ya existe
            if (repo.findByCorreo(dto.getCorreo()).isPresent()) {
                throw new RuntimeException("El email ya está registrado: " + dto.getCorreo());
            }

            Usuario entity = mapper.map(dto, Usuario.class);
            entity.setRol("usuario");
            entity.setPassword(encoder.encode(dto.getPassword()));
            entity.setFechaCreacion(LocalDateTime.now());

            Usuario savedUser = repo.save(entity);
            logger.info("✅ Usuario guardado en base de datos: {}", savedUser.getCorreo());

            // NUEVO: Crear y publicar evento de registro
            UsuarioRegistroEvent registroEvent = new UsuarioRegistroEvent(
                    savedUser.getId(),
                    savedUser.getNombre(),
                    savedUser.getCorreo(),
                    savedUser.getRol(),
                    savedUser.getFechaCreacion()
            );

            // Publicar evento que disparará el envío de email
            messagePublisher.publishUserRegistration(registroEvent);
            logger.info("📡 Evento de registro publicado para: {}", savedUser.getCorreo());

            return mapper.map(savedUser, UsuarioDto.class);

        } catch (Exception e) {
            logger.error("❌ Error creando usuario: {}", e.getMessage(), e);
            throw new RuntimeException("Error creando usuario: " + e.getMessage());
        }
    }

    @Override
    public List<UsuarioDto> findAll() {
        return repo.findAll().stream()
                .map(u -> mapper.map(u, UsuarioDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDto findById(Long id) {
        Usuario u = repo.findById(id).orElseThrow(() ->
                new RuntimeException("Usuario no encontrado con ID: " + id));
        return mapper.map(u, UsuarioDto.class);
    }

    @Override
    public UsuarioDto update(Long id, UsuarioDto dto) {
        Usuario u = repo.findById(id).orElseThrow(() ->
                new RuntimeException("Usuario no encontrado con ID: " + id));

        // No mapear password si no se proporciona
        String originalPassword = u.getPassword();
        mapper.map(dto, u);

        // Si no se proporciona nueva password, mantener la original
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            u.setPassword(originalPassword);
        } else {
            u.setPassword(encoder.encode(dto.getPassword()));
        }

        return mapper.map(repo.save(u), UsuarioDto.class);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        repo.deleteById(id);
        logger.info("🗑️ Usuario eliminado con ID: {}", id);
    }

    @Override
    @Transactional
    public UsuarioDto changeRole(Long id, String nuevoRol) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        String rolAnterior = u.getRol();
        u.setRol(nuevoRol);
        Usuario updated = repo.save(u);

        logger.info("🔄 Rol cambiado para usuario {}: {} -> {}",
                u.getCorreo(), rolAnterior, nuevoRol);

        return mapper.map(updated, UsuarioDto.class);
    }
}