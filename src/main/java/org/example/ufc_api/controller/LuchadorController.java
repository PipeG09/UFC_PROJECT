package org.example.ufc_api.controller;

import org.example.ufc_api.dto.LuchadorDto;
import org.example.ufc_api.service.LuchadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/luchadores")
public class LuchadorController {

    @Autowired
    private LuchadorService service;

    @PostMapping
    public LuchadorDto create(@RequestBody LuchadorDto dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<LuchadorDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public LuchadorDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public LuchadorDto update(@PathVariable Long id, @RequestBody LuchadorDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

