package org.example.ufc_api.service.impl;

import org.example.ufc_api.dto.UsuarioDto;
import org.example.ufc_api.model.Usuario;
import org.example.ufc_api.repository.UsuarioRepository;
import org.example.ufc_api.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired private UsuarioRepository repo;
    @Autowired private ModelMapper mapper;

    @Override
    public UsuarioDto create(UsuarioDto dto) {
        Usuario entity = mapper.map(dto, Usuario.class);
        entity.setFechaCreacion(LocalDateTime.now());
        return mapper.map(repo.save(entity), UsuarioDto.class);
    }

    @Override
    public List<UsuarioDto> findAll() {
        return repo.findAll().stream()
                .map(u -> mapper.map(u, UsuarioDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDto findById(Long id) {
        Usuario u = repo.findById(id).orElseThrow();
        return mapper.map(u, UsuarioDto.class);
    }

    @Override
    public UsuarioDto update(Long id, UsuarioDto dto) {
        Usuario u = repo.findById(id).orElseThrow();
        mapper.map(dto, u);
        return mapper.map(repo.save(u), UsuarioDto.class);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
