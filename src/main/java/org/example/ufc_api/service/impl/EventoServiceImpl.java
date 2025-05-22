package org.example.ufc_api.service.impl;

import org.example.ufc_api.dto.EventoDto;
import org.example.ufc_api.model.Evento;
import org.example.ufc_api.repository.EventoRepository;
import org.example.ufc_api.service.EventoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoServiceImpl implements EventoService {

    @Autowired
    private EventoRepository repository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public EventoDto create(EventoDto dto) {
        Evento entity = mapper.map(dto, Evento.class);
        Evento saved = repository.save(entity);
        return mapper.map(saved, EventoDto.class);
    }

    @Override
    public List<EventoDto> findAll() {
        return repository.findAll()
                .stream()
                .map(e -> mapper.map(e, EventoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public EventoDto findById(Long id) {
        Evento e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        return mapper.map(e, EventoDto.class);
    }

    @Override
    public EventoDto update(Long id, EventoDto dto) {
        Evento e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        mapper.map(dto, e);
        Evento updated = repository.save(e);
        return mapper.map(updated, EventoDto.class);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
