package org.example.ufc_api.service;

import org.example.ufc_api.dto.UsuarioDto;
import java.util.List;

public interface UsuarioService {
    UsuarioDto create(UsuarioDto dto);
    List<UsuarioDto> findAll();
    UsuarioDto findById(Long id);
    UsuarioDto update(Long id, UsuarioDto dto);
    void delete(Long id);
    UsuarioDto changeRole(Long id, String nuevoRol);
}
