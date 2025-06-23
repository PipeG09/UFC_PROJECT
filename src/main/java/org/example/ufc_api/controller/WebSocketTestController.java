package org.example.ufc_api.controller;

import org.example.ufc_api.model.Estadistica;
import org.example.ufc_api.model.Pelea;
import org.example.ufc_api.repository.EstadisticaRepository;
import org.example.ufc_api.repository.PeleaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class WebSocketTestController {

    @Autowired
    private PeleaRepository peleaRepository;

    @Autowired
    private EstadisticaRepository estadisticaRepository;

    /**
     * Test rápido - verificar si hay datos
     */
    @GetMapping("/quick-check")
    public Map<String, Object> quickCheck() {
        Map<String, Object> result = new HashMap<>();

        // Contar peleas activas
        List<Pelea> peleasActivas = peleaRepository.findByFinalizadaFalse();
        result.put("peleasActivas", peleasActivas.size());

        if (!peleasActivas.isEmpty()) {
            Pelea primera = peleasActivas.get(0);
            result.put("primeraActiva", Map.of(
                    "id", primera.getId(),
                    "evento", primera.getEvento().getNombre(),
                    "azul", primera.getAzul().getNombre(),
                    "rojo", primera.getRojo().getNombre()
            ));

            // Contar estadísticas
            List<Estadistica> stats = estadisticaRepository.findByPeleaId(primera.getId());
            result.put("totalEstadisticas", stats.size());

            // Estadísticas por luchador
            List<Estadistica> statsAzul = estadisticaRepository
                    .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(primera.getId(), primera.getAzul().getId());
            List<Estadistica> statsRojo = estadisticaRepository
                    .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(primera.getId(), primera.getRojo().getId());

            result.put("statsAzul", statsAzul.size());
            result.put("statsRojo", statsRojo.size());

            // Calcular totales
            int totalAzulGolpes = statsAzul.stream()
                    .mapToInt(s -> s.getGolpesConectados() != null ? s.getGolpesConectados() : 0)
                    .sum();
            int totalRojoGolpes = statsRojo.stream()
                    .mapToInt(s -> s.getGolpesConectados() != null ? s.getGolpesConectados() : 0)
                    .sum();

            result.put("totales", Map.of(
                    "azulGolpes", totalAzulGolpes,
                    "rojoGolpes", totalRojoGolpes
            ));
        }

        result.put("timestamp", LocalDateTime.now());
        return result;
    }

    /**
     * Crear datos mínimos para test
     */
    @PostMapping("/create-minimal-data/{peleaId}")
    public Map<String, Object> createMinimalData(@PathVariable Long peleaId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Pelea pelea = peleaRepository.findById(peleaId)
                    .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));

            // Crear una estadística simple para cada luchador
            Estadistica statAzul = new Estadistica();
            statAzul.setPelea(pelea);
            statAzul.setLuchador(pelea.getAzul());
            statAzul.setRound(1);
            statAzul.setGolpesConectados(10);
            statAzul.setDerribos(1);
            statAzul.setControlJaulaSegundos(60);
            statAzul.setTimestamp(LocalDateTime.now().minusMinutes(2));
            estadisticaRepository.save(statAzul);

            Estadistica statRojo = new Estadistica();
            statRojo.setPelea(pelea);
            statRojo.setLuchador(pelea.getRojo());
            statRojo.setRound(1);
            statRojo.setGolpesConectados(8);
            statRojo.setDerribos(2);
            statRojo.setControlJaulaSegundos(45);
            statRojo.setTimestamp(LocalDateTime.now().minusMinutes(1));
            estadisticaRepository.save(statRojo);

            result.put("status", "success");
            result.put("message", "Datos mínimos creados");
            result.put("estadisticasCreadas", 2);

        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
        }

        return result;
    }
}