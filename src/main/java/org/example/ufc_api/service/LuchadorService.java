package org.example.ufc_api.service;

import org.example.ufc_api.dto.LuchadorDto;
import java.util.List;

public interface LuchadorService {
    LuchadorDto create(LuchadorDto dto);
    List<LuchadorDto> findAll();
    LuchadorDto findById(Long id);
    LuchadorDto update(Long id, LuchadorDto dto);
    void delete(Long id);
}

