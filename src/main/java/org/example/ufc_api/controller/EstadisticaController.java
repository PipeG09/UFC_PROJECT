package org.example.ufc_api.controller;

import org.example.ufc_api.dto.EstadisticaDto;
import org.example.ufc_api.service.EstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaController {

    @Autowired
    private EstadisticaService service;

    @PostMapping
    public EstadisticaDto create(@RequestBody EstadisticaDto dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<EstadisticaDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public EstadisticaDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public EstadisticaDto update(@PathVariable Long id, @RequestBody EstadisticaDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

