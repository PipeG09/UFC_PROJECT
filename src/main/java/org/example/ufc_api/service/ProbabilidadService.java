package org.example.ufc_api.service;

import org.example.ufc_api.dto.ProbabilidadDto;
import java.util.List;

public interface ProbabilidadService {
    ProbabilidadDto create(ProbabilidadDto dto);
    List<ProbabilidadDto> findAll();
    ProbabilidadDto findById(Long id);
    ProbabilidadDto update(Long id, ProbabilidadDto dto);
    void delete(Long id);
}
