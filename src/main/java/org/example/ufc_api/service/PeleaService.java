package org.example.ufc_api.service;

import org.example.ufc_api.dto.PeleaDto;
import java.util.List;

public interface PeleaService {
    PeleaDto create(PeleaDto dto);
    List<PeleaDto> findAll();
    PeleaDto findById(Long id);
    PeleaDto update(Long id, PeleaDto dto);
    void delete(Long id);
}
