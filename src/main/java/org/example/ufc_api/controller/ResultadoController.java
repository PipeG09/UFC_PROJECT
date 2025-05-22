package org.example.ufc_api.controller;

import org.example.ufc_api.dto.ResultadoDto;
import org.example.ufc_api.service.ResultadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resultados")
public class ResultadoController {

    @Autowired
    private ResultadoService service;

    @PostMapping
    public ResultadoDto create(@RequestBody ResultadoDto dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<ResultadoDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResultadoDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ResultadoDto update(@PathVariable Long id, @RequestBody ResultadoDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
