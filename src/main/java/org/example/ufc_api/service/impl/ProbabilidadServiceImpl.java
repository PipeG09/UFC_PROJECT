package org.example.ufc_api.service.impl;

import org.example.ufc_api.dto.ProbabilidadDto;
import org.example.ufc_api.model.Probabilidad;
import org.example.ufc_api.model.Pelea;
import org.example.ufc_api.model.Luchador;
import org.example.ufc_api.repository.ProbabilidadRepository;
import org.example.ufc_api.repository.PeleaRepository;
import org.example.ufc_api.repository.LuchadorRepository;
import org.example.ufc_api.service.ProbabilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProbabilidadServiceImpl implements ProbabilidadService {

    @Autowired
    private ProbabilidadRepository probRepo;

    @Autowired
    private PeleaRepository peleaRepo;

    @Autowired
    private LuchadorRepository luchadorRepo;

    @Override
    public ProbabilidadDto create(ProbabilidadDto dto) {
        Probabilidad entity = new Probabilidad();

        // recargar asociaciones
        Pelea pelea = peleaRepo.findById(dto.getPeleaId())
                .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));
        Luchador luchador = luchadorRepo.findById(dto.getLuchadorId())
                .orElseThrow(() -> new RuntimeException("Luchador no encontrado"));

        entity.setPelea(pelea);
        entity.setLuchador(luchador);
        entity.setProbabilidad(dto.getProbabilidad());
        entity.setTimestamp(LocalDateTime.now());

        Probabilidad saved = probRepo.save(entity);
        return mapToDto(saved);
    }

    @Override
    public List<ProbabilidadDto> findAll() {
        return probRepo.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProbabilidadDto findById(Long id) {
        Probabilidad p = probRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Probabilidad no encontrada"));
        return mapToDto(p);
    }

    @Override
    public ProbabilidadDto update(Long id, ProbabilidadDto dto) {
        Probabilidad entity = probRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Probabilidad no encontrada"));

        // recargar asociaciones si cambian
        if (!entity.getPelea().getId().equals(dto.getPeleaId())) {
            Pelea pelea = peleaRepo.findById(dto.getPeleaId())
                    .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));
            entity.setPelea(pelea);
        }
        if (!entity.getLuchador().getId().equals(dto.getLuchadorId())) {
            Luchador luchador = luchadorRepo.findById(dto.getLuchadorId())
                    .orElseThrow(() -> new RuntimeException("Luchador no encontrado"));
            entity.setLuchador(luchador);
        }

        entity.setProbabilidad(dto.getProbabilidad());
        // no tocamos timestamp para preservar el original

        Probabilidad updated = probRepo.save(entity);
        return mapToDto(updated);
    }

    @Override
    public void delete(Long id) {
        probRepo.deleteById(id);
    }

    // helper para mapear a DTO
    private ProbabilidadDto mapToDto(Probabilidad p) {
        ProbabilidadDto dto = new ProbabilidadDto();
        dto.setId(p.getId());
        dto.setPeleaId(p.getPelea().getId());
        dto.setLuchadorId(p.getLuchador().getId());
        dto.setProbabilidad(p.getProbabilidad());
        dto.setTimestamp(p.getTimestamp());
        return dto;
    }
}

