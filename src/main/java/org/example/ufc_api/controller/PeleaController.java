package org.example.ufc_api.controller;

import org.example.ufc_api.dto.PeleaDto;
import org.example.ufc_api.service.PeleaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peleas")
public class PeleaController {

    @Autowired
    private PeleaService service;

    @PostMapping
    public PeleaDto create(@RequestBody PeleaDto dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<PeleaDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PeleaDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public PeleaDto update(@PathVariable Long id, @RequestBody PeleaDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
