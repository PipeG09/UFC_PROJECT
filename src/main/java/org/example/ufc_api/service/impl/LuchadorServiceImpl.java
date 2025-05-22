package org.example.ufc_api.service.impl;

import org.example.ufc_api.dto.LuchadorDto;
import org.example.ufc_api.model.Luchador;
import org.example.ufc_api.repository.LuchadorRepository;
import org.example.ufc_api.service.LuchadorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LuchadorServiceImpl implements LuchadorService {

    @Autowired
    private LuchadorRepository repository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public LuchadorDto create(LuchadorDto dto) {
        Luchador entity = mapper.map(dto, Luchador.class);
        Luchador saved = repository.save(entity);
        return mapper.map(saved, LuchadorDto.class);
    }

    @Override
    public List<LuchadorDto> findAll() {
        return repository.findAll()
                .stream()
                .map(l -> mapper.map(l, LuchadorDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public LuchadorDto findById(Long id) {
        Luchador l = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Luchador no encontrado"));
        return mapper.map(l, LuchadorDto.class);
    }

    @Override
    public LuchadorDto update(Long id, LuchadorDto dto) {
        Luchador l = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Luchador no encontrado"));
        mapper.map(dto, l);
        Luchador updated = repository.save(l);
        return mapper.map(updated, LuchadorDto.class);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

