package org.example.ufc_api.controller;

import org.example.ufc_api.dto.UsuarioDto;
import org.example.ufc_api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired private UsuarioService service;

    @PostMapping
    public UsuarioDto create(@RequestBody UsuarioDto dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<UsuarioDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public UsuarioDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public UsuarioDto update(@PathVariable Long id, @RequestBody UsuarioDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}