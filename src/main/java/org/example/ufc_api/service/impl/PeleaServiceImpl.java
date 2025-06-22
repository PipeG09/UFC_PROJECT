package org.example.ufc_api.service.impl;

import org.example.ufc_api.dto.PeleaDto;
import org.example.ufc_api.model.Evento;
import org.example.ufc_api.model.Luchador;
import org.example.ufc_api.model.Pelea;
import org.example.ufc_api.repository.EventoRepository;
import org.example.ufc_api.repository.LuchadorRepository;
import org.example.ufc_api.repository.PeleaRepository;
import org.example.ufc_api.service.PeleaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PeleaServiceImpl implements PeleaService {

    @Autowired
    private PeleaRepository peleaRepo;

    @Autowired
    private EventoRepository eventoRepo;

    @Autowired
    private LuchadorRepository luchadorRepo;

    @Autowired
    private ModelMapper mapper;

    @Override
    public PeleaDto create(PeleaDto dto) {
        Pelea entidad = new Pelea();
        // cargar asociaciones
        Evento ev = eventoRepo.findById(dto.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        Luchador az = luchadorRepo.findById(dto.getLuchadorAzulId())
                .orElseThrow(() -> new RuntimeException("Luchador azul no encontrado"));
        Luchador ro = luchadorRepo.findById(dto.getLuchadorRojoId())
                .orElseThrow(() -> new RuntimeException("Luchador rojo no encontrado"));

        entidad.setEvento(ev);
        entidad.setAzul(az);
        entidad.setRojo(ro);
        entidad.setFecha(dto.getFecha());
        entidad.setFinalizada(dto.getFinalizada() != null ? dto.getFinalizada() : false);

        Pelea saved = peleaRepo.save(entidad);
        return mapToDto(saved);
    }

    @Override
    public List<PeleaDto> findAll() {
        return peleaRepo.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PeleaDto findById(Long id) {
        return peleaRepo.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));
    }

    // NUEVAS IMPLEMENTACIONES
    @Override
    public List<PeleaDto> findByEventoId(Long eventoId) {
        List<Pelea> peleas = peleaRepo.findAll()
                .stream()
                .filter(pelea -> pelea.getEvento().getId().equals(eventoId))
                .collect(Collectors.toList());

        return peleas.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PeleaDto> findLiveFights() {
        return peleaRepo.findByFinalizadaFalse()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PeleaDto> findFinishedFights() {
        return peleaRepo.findByFinalizadaTrue()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PeleaDto update(Long id, PeleaDto dto) {
        Pelea entidad = peleaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));
        // recargar asociaciones si cambian
        if (!entidad.getEvento().getId().equals(dto.getEventoId())) {
            Evento ev = eventoRepo.findById(dto.getEventoId())
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
            entidad.setEvento(ev);
        }
        if (!entidad.getAzul().getId().equals(dto.getLuchadorAzulId())) {
            Luchador az = luchadorRepo.findById(dto.getLuchadorAzulId())
                    .orElseThrow(() -> new RuntimeException("Luchador azul no encontrado"));
            entidad.setAzul(az);
        }
        if (!entidad.getRojo().getId().equals(dto.getLuchadorRojoId())) {
            Luchador ro = luchadorRepo.findById(dto.getLuchadorRojoId())
                    .orElseThrow(() -> new RuntimeException("Luchador rojo no encontrado"));
            entidad.setRojo(ro);
        }
        entidad.setFecha(dto.getFecha());
        entidad.setFinalizada(dto.getFinalizada());

        Pelea updated = peleaRepo.save(entidad);
        return mapToDto(updated);
    }

    @Override
    public void delete(Long id) {
        peleaRepo.deleteById(id);
    }

    // método auxiliar para mapear
    private PeleaDto mapToDto(Pelea p) {
        PeleaDto dto = new PeleaDto();
        dto.setId(p.getId());
        dto.setEventoId(p.getEvento().getId());
        dto.setLuchadorAzulId(p.getAzul().getId());
        dto.setLuchadorRojoId(p.getRojo().getId());
        dto.setFecha(p.getFecha());
        dto.setFinalizada(p.getFinalizada());
        return dto;
    }
}
