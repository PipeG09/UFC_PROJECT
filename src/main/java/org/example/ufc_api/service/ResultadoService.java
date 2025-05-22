package org.example.ufc_api.service;

import org.example.ufc_api.dto.ResultadoDto;
import java.util.List;

public interface ResultadoService {
    ResultadoDto create(ResultadoDto dto);
    List<ResultadoDto> findAll();
    ResultadoDto findById(Long id);
    ResultadoDto update(Long id, ResultadoDto dto);
    void delete(Long id);
}
