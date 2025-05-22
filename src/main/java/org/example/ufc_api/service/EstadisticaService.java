package org.example.ufc_api.service;

import org.example.ufc_api.dto.EstadisticaDto;
import java.util.List;

public interface EstadisticaService {
    EstadisticaDto create(EstadisticaDto dto);
    List<EstadisticaDto> findAll();
    EstadisticaDto findById(Long id);
    EstadisticaDto update(Long id, EstadisticaDto dto);
    void delete(Long id);
}

