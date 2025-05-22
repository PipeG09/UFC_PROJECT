package org.example.ufc_api.service;

import org.example.ufc_api.dto.EventoDto;
import java.util.List;

public interface EventoService {
    EventoDto create(EventoDto dto);
    List<EventoDto> findAll();
    EventoDto findById(Long id);
    EventoDto update(Long id, EventoDto dto);
    void delete(Long id);
}
