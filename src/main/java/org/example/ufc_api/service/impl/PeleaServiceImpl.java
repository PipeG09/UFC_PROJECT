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

import java.time.LocalDateTime;
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
        try {
            System.out.println("🕐 BACKEND - Recibiendo solicitud para crear pelea:");
            System.out.println("  DTO recibido: " + dto);
            System.out.println("  Fecha recibida: " + dto.getFecha());
            System.out.println("  Evento ID: " + dto.getEventoId());
            System.out.println("  Luchador Azul ID: " + dto.getLuchadorAzulId());
            System.out.println("  Luchador Rojo ID: " + dto.getLuchadorRojoId());
            System.out.println("  Finalizada: " + dto.getFinalizada());

            Pelea entidad = new Pelea();

            // Cargar y verificar evento
            System.out.println("🔍 Buscando evento ID: " + dto.getEventoId());
            Evento ev = eventoRepo.findById(dto.getEventoId())
                    .orElseThrow(() -> {
                        System.err.println("❌ Evento no encontrado: " + dto.getEventoId());
                        return new RuntimeException("Evento no encontrado: " + dto.getEventoId());
                    });
            System.out.println("✅ Evento encontrado: " + ev.getNombre());

            // Cargar y verificar luchadores
            System.out.println("🔍 Buscando luchador azul ID: " + dto.getLuchadorAzulId());
            Luchador az = luchadorRepo.findById(dto.getLuchadorAzulId())
                    .orElseThrow(() -> {
                        System.err.println("❌ Luchador azul no encontrado: " + dto.getLuchadorAzulId());
                        return new RuntimeException("Luchador azul no encontrado: " + dto.getLuchadorAzulId());
                    });
            System.out.println("✅ Luchador azul encontrado: " + az.getNombre());

            System.out.println("🔍 Buscando luchador rojo ID: " + dto.getLuchadorRojoId());
            Luchador ro = luchadorRepo.findById(dto.getLuchadorRojoId())
                    .orElseThrow(() -> {
                        System.err.println("❌ Luchador rojo no encontrado: " + dto.getLuchadorRojoId());
                        return new RuntimeException("Luchador rojo no encontrado: " + dto.getLuchadorRojoId());
                    });
            System.out.println("✅ Luchador rojo encontrado: " + ro.getNombre());

            // Configurar la entidad
            entidad.setEvento(ev);
            entidad.setAzul(az);
            entidad.setRojo(ro);
            entidad.setFecha(dto.getFecha());
            entidad.setFinalizada(dto.getFinalizada() != null ? dto.getFinalizada() : false);

            System.out.println("💾 Guardando pelea en base de datos...");
            System.out.println("  Fecha a guardar: " + entidad.getFecha());
            System.out.println("  Finalizada: " + entidad.getFinalizada());

            Pelea saved = peleaRepo.save(entidad);

            System.out.println("✅ Pelea guardada exitosamente:");
            System.out.println("  ID: " + saved.getId());
            System.out.println("  Fecha guardada: " + saved.getFecha());
            System.out.println("  Evento: " + saved.getEvento().getNombre());
            System.out.println("  Luchadores: " + saved.getAzul().getNombre() + " vs " + saved.getRojo().getNombre());

            return mapToDto(saved);

        } catch (Exception e) {
            System.err.println("❌ ERROR en create(): " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creando pelea: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PeleaDto> findAll() {
        List<Pelea> todasLasPeleas = peleaRepo.findAll();
        System.out.println("📋 Total de peleas en BD: " + todasLasPeleas.size());

        LocalDateTime now = LocalDateTime.now();
        System.out.println("🕐 Hora actual: " + now);

        // Debug: mostrar estado de cada pelea
        for (Pelea pelea : todasLasPeleas) {
            String estado = determinarEstadoPelea(pelea, now);
            System.out.println("  - Pelea ID: " + pelea.getId() +
                    ", Finalizada: " + pelea.getFinalizada() +
                    ", Fecha: " + pelea.getFecha() +
                    ", Estado: " + estado);
        }

        return todasLasPeleas.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PeleaDto findById(Long id) {
        return peleaRepo.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));
    }

    @Override
    public List<PeleaDto> findByEventoId(Long eventoId) {
        System.out.println("🔍 Buscando peleas para evento ID: " + eventoId);
        List<Pelea> peleas = peleaRepo.findByEventoId(eventoId);
        System.out.println("📊 Encontradas " + peleas.size() + " peleas para evento " + eventoId);

        return peleas.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // NUEVO: Peleas realmente EN VIVO (considerando fecha y hora)
    @Override
    public List<PeleaDto> findLiveFights() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("🔴 Buscando peleas EN VIVO (fecha <= " + now + " && finalizada = false)...");

        List<Pelea> peleasEnVivo = peleaRepo.findLiveFights(now);

        System.out.println("📺 Encontradas " + peleasEnVivo.size() + " peleas EN VIVO");

        // Debug detallado
        for (Pelea pelea : peleasEnVivo) {
            System.out.println("  - Pelea EN VIVO ID: " + pelea.getId() +
                    ", Fecha: " + pelea.getFecha() +
                    ", Finalizada: " + pelea.getFinalizada() +
                    ", Evento: " + pelea.getEvento().getNombre());
        }

        return peleasEnVivo.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // NUEVO: Peleas FUTURAS (no han empezado aún)
    @Override
    public List<PeleaDto> findUpcomingFights() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("⏰ Buscando peleas FUTURAS (fecha > " + now + " && finalizada = false)...");

        List<Pelea> peleasFuturas = peleaRepo.findUpcomingFights(now);

        System.out.println("📅 Encontradas " + peleasFuturas.size() + " peleas FUTURAS");

        return peleasFuturas.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // NUEVO: Todas las peleas activas (futuras + en vivo)
    @Override
    public List<PeleaDto> findAllActiveFights() {
        System.out.println("🎯 Buscando todas las peleas ACTIVAS (finalizada = false)...");

        List<Pelea> peleasActivas = peleaRepo.findByFinalizadaFalse();

        System.out.println("🔄 Encontradas " + peleasActivas.size() + " peleas ACTIVAS");

        return peleasActivas.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PeleaDto> findFinishedFights() {
        System.out.println("🏁 Buscando peleas FINALIZADAS (finalizada = true)...");

        List<Pelea> peleasFinalizadas = peleaRepo.findByFinalizadaTrue();

        System.out.println("✅ Encontradas " + peleasFinalizadas.size() + " peleas FINALIZADAS");

        return peleasFinalizadas.stream()
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

        System.out.println("🔄 Pelea actualizada ID: " + updated.getId() +
                ", Fecha: " + updated.getFecha() +
                ", Finalizada: " + updated.getFinalizada());

        return mapToDto(updated);
    }

    @Override
    public void delete(Long id) {
        System.out.println("🗑️ Eliminando pelea ID: " + id);
        peleaRepo.deleteById(id);
    }

    // Método auxiliar para mapear
    private PeleaDto mapToDto(Pelea p) {
        try {
            PeleaDto dto = new PeleaDto();
            dto.setId(p.getId());
            dto.setEventoId(p.getEvento().getId());
            dto.setLuchadorAzulId(p.getAzul().getId());
            dto.setLuchadorRojoId(p.getRojo().getId());
            dto.setFecha(p.getFecha());
            dto.setFinalizada(p.getFinalizada());
            return dto;
        } catch (Exception e) {
            System.err.println("❌ ERROR mapeando pelea " + p.getId() + ": " + e.getMessage());
            throw new RuntimeException("Error mapeando pelea", e);
        }
    }

    // Método auxiliar para determinar estado de una pelea
    private String determinarEstadoPelea(Pelea pelea, LocalDateTime now) {
        if (pelea.getFinalizada()) {
            return "FINALIZADA";
        } else if (pelea.getFecha().isAfter(now)) {
            return "FUTURA";
        } else {
            return "EN_VIVO";
        }
    }
}