package org.example.ufc_api.controller;

import org.example.ufc_api.dto.PeleaDto;
import org.example.ufc_api.service.PeleaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peleas")
public class PeleaController {

    @Autowired
    private PeleaService service;

    @PostMapping
    public PeleaDto create(@RequestBody PeleaDto dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<PeleaDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PeleaDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    // Endpoint para obtener peleas por evento
    @GetMapping("/evento/{eventoId}")
    public List<PeleaDto> getByEventoId(@PathVariable Long eventoId) {
        System.out.println("🔍 Buscando peleas para evento ID: " + eventoId);
        List<PeleaDto> peleas = service.findByEventoId(eventoId);
        System.out.println("📊 Encontradas " + peleas.size() + " peleas para evento " + eventoId);
        return peleas;
    }

    // CORREGIDO: Endpoint para obtener solo peleas EN VIVO AHORA (fecha <= now && !finalizada)
    @GetMapping("/live")
    public List<PeleaDto> getLiveFights() {
        System.out.println("🔴 Solicitando peleas EN VIVO (que ya empezaron)...");
        List<PeleaDto> peleasEnVivo = service.findLiveFights();
        System.out.println("📺 Devolviendo " + peleasEnVivo.size() + " peleas EN VIVO");
        return peleasEnVivo;
    }

    // NUEVO: Endpoint para obtener peleas FUTURAS (fecha > now && !finalizada)
    @GetMapping("/upcoming")
    public List<PeleaDto> getUpcomingFights() {
        System.out.println("⏰ Solicitando peleas FUTURAS (que aún no empezaron)...");
        List<PeleaDto> peleasFuturas = service.findUpcomingFights();
        System.out.println("📅 Devolviendo " + peleasFuturas.size() + " peleas FUTURAS");
        return peleasFuturas;
    }

    // NUEVO: Endpoint para obtener todas las peleas activas (futuras + en vivo)
    @GetMapping("/active")
    public List<PeleaDto> getActiveFights() {
        System.out.println("🎯 Solicitando todas las peleas ACTIVAS...");
        List<PeleaDto> peleasActivas = service.findAllActiveFights();
        System.out.println("🔄 Devolviendo " + peleasActivas.size() + " peleas ACTIVAS");
        return peleasActivas;
    }

    // Endpoint para obtener peleas finalizadas
    @GetMapping("/finished")
    public List<PeleaDto> getFinishedFights() {
        System.out.println("🏁 Solicitando peleas FINALIZADAS...");
        List<PeleaDto> peleasFinalizadas = service.findFinishedFights();
        System.out.println("✅ Devolviendo " + peleasFinalizadas.size() + " peleas FINALIZADAS");
        return peleasFinalizadas;
    }

    @PutMapping("/{id}")
    public PeleaDto update(@PathVariable Long id, @RequestBody PeleaDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}