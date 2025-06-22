package org.example.ufc_api.service;

import org.example.ufc_api.dto.PeleaDto;
import java.util.List;

public interface PeleaService {
    PeleaDto create(PeleaDto dto);
    List<PeleaDto> findAll();
    PeleaDto findById(Long id);
    PeleaDto update(Long id, PeleaDto dto);
    void delete(Long id);

    // Métodos existentes
    List<PeleaDto> findByEventoId(Long eventoId);
    List<PeleaDto> findFinishedFights();

    // NUEVOS: Métodos que consideran la fecha
    List<PeleaDto> findLiveFights();        // En vivo AHORA (fecha <= now && !finalizada)
    List<PeleaDto> findUpcomingFights();    // Futuras (fecha > now && !finalizada)
    List<PeleaDto> findAllActiveFights();   // Todas las no finalizadas (futuras + en vivo)
}