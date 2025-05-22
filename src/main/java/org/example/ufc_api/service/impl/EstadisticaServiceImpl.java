package org.example.ufc_api.service.impl;

import org.example.ufc_api.dto.EstadisticaDto;
import org.example.ufc_api.model.Estadistica;
import org.example.ufc_api.model.Pelea;
import org.example.ufc_api.model.Luchador;
import org.example.ufc_api.repository.EstadisticaRepository;
import org.example.ufc_api.repository.PeleaRepository;
import org.example.ufc_api.repository.LuchadorRepository;
import org.example.ufc_api.service.EstadisticaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadisticaServiceImpl implements EstadisticaService {

    @Autowired
    private EstadisticaRepository estadisticaRepo;

    @Autowired
    private PeleaRepository peleaRepo;

    @Autowired
    private LuchadorRepository luchadorRepo;

    @Autowired
    private ModelMapper mapper;

    @Override
    public EstadisticaDto create(EstadisticaDto dto) {
        Pelea pelea = peleaRepo.findById(dto.getPeleaId())
                .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));
        Luchador luchador = luchadorRepo.findById(dto.getLuchadorId())
                .orElseThrow(() -> new RuntimeException("Luchador no encontrado"));

        Estadistica e = new Estadistica();
        e.setPelea(pelea);
        e.setLuchador(luchador);
        e.setRound(dto.getRound());
        e.setGolpesConectados(dto.getGolpesConectados());
        e.setDerribos(dto.getDerribos());
        e.setControlJaulaSegundos(dto.getControlJaulaSegundos());
        e.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : java.time.LocalDateTime.now());

        Estadistica saved = estadisticaRepo.save(e);
        EstadisticaDto out = mapper.map(saved, EstadisticaDto.class);
        out.setPeleaId(saved.getPelea().getId());
        out.setLuchadorId(saved.getLuchador().getId());
        return out;
    }

    @Override
    public List<EstadisticaDto> findAll() {
        return estadisticaRepo.findAll().stream().map(s -> {
            EstadisticaDto dto = mapper.map(s, EstadisticaDto.class);
            dto.setPeleaId(s.getPelea().getId());
            dto.setLuchadorId(s.getLuchador().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public EstadisticaDto findById(Long id) {
        Estadistica s = estadisticaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Estadistica no encontrada"));
        EstadisticaDto dto = mapper.map(s, EstadisticaDto.class);
        dto.setPeleaId(s.getPelea().getId());
        dto.setLuchadorId(s.getLuchador().getId());
        return dto;
    }

    @Override
    public EstadisticaDto update(Long id, EstadisticaDto dto) {
        Estadistica s = estadisticaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Estadistica no encontrada"));

        Pelea pelea = peleaRepo.findById(dto.getPeleaId())
                .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));
        Luchador luchador = luchadorRepo.findById(dto.getLuchadorId())
                .orElseThrow(() -> new RuntimeException("Luchador no encontrado"));

        s.setPelea(pelea);
        s.setLuchador(luchador);
        s.setRound(dto.getRound());
        s.setGolpesConectados(dto.getGolpesConectados());
        s.setDerribos(dto.getDerribos());
        s.setControlJaulaSegundos(dto.getControlJaulaSegundos());
        s.setTimestamp(dto.getTimestamp());

        Estadistica updated = estadisticaRepo.save(s);
        EstadisticaDto out = mapper.map(updated, EstadisticaDto.class);
        out.setPeleaId(updated.getPelea().getId());
        out.setLuchadorId(updated.getLuchador().getId());
        return out;
    }

    @Override
    public void delete(Long id) {
        estadisticaRepo.deleteById(id);
    }
}

