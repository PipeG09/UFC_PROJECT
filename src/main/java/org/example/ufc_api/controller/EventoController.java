package org.example.ufc_api.controller;

import org.example.ufc_api.dto.EventoDto;
import org.example.ufc_api.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService service;

    @PostMapping
    public EventoDto create(@RequestBody EventoDto dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<EventoDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public EventoDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public EventoDto update(@PathVariable Long id, @RequestBody EventoDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
