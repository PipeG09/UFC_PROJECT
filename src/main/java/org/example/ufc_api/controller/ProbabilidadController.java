package org.example.ufc_api.controller;

import org.example.ufc_api.dto.ProbabilidadDto;
import org.example.ufc_api.service.ProbabilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/probabilidades")
public class ProbabilidadController {
    @Autowired
    private ProbabilidadService service;

    @PostMapping
    public ProbabilidadDto create(@RequestBody ProbabilidadDto dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<ProbabilidadDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProbabilidadDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ProbabilidadDto update(@PathVariable Long id, @RequestBody ProbabilidadDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
