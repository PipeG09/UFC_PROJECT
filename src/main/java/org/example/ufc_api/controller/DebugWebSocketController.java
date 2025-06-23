package org.example.ufc_api.controller;

import org.example.ufc_api.model.Estadistica;
import org.example.ufc_api.model.Pelea;
import org.example.ufc_api.model.Probabilidad;
import org.example.ufc_api.repository.EstadisticaRepository;
import org.example.ufc_api.repository.PeleaRepository;
import org.example.ufc_api.repository.ProbabilidadRepository;
import org.example.ufc_api.websocket.LiveFightHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugWebSocketController {

    @Autowired
    private PeleaRepository peleaRepository;

    @Autowired
    private EstadisticaRepository estadisticaRepository;

    @Autowired
    private ProbabilidadRepository probabilidadRepository;

    @Autowired
    private LiveFightHandler liveFightHandler;

    /**
     * Verificar datos en la base de datos para WebSocket
     */
    @GetMapping("/websocket-data")
    public Map<String, Object> checkWebSocketData() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar peleas activas
            List<Pelea> peleasActivas = peleaRepository.findByFinalizadaFalse();
            response.put("peleasActivas", peleasActivas.size());
            response.put("peleasActivasDetalle", peleasActivas.stream().map(p -> Map.of(
                    "id", p.getId(),
                    "evento", p.getEvento().getNombre(),
                    "azul", p.getAzul().getNombre(),
                    "rojo", p.getRojo().getNombre(),
                    "fecha", p.getFecha(),
                    "finalizada", p.getFinalizada()
            )).toList());

            if (!peleasActivas.isEmpty()) {
                Pelea primeraActiva = peleasActivas.get(0);
                Long peleaId = primeraActiva.getId();

                // Verificar estadísticas
                List<Estadistica> todasStats = estadisticaRepository.findByPeleaId(peleaId);
                List<Estadistica> statsAzul = estadisticaRepository
                        .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(peleaId, primeraActiva.getAzul().getId());
                List<Estadistica> statsRojo = estadisticaRepository
                        .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(peleaId, primeraActiva.getRojo().getId());

                response.put("estadisticas", Map.of(
                        "total", todasStats.size(),
                        "azul", statsAzul.size(),
                        "rojo", statsRojo.size(),
                        "detalleAzul", statsAzul.stream().limit(3).map(s -> Map.of(
                                "id", s.getId(),
                                "golpes", s.getGolpesConectados(),
                                "derribos", s.getDerribos(),
                                "control", s.getControlJaulaSegundos(),
                                "round", s.getRound(),
                                "timestamp", s.getTimestamp()
                        )).toList(),
                        "detalleRojo", statsRojo.stream().limit(3).map(s -> Map.of(
                                "id", s.getId(),
                                "golpes", s.getGolpesConectados(),
                                "derribos", s.getDerribos(),
                                "control", s.getControlJaulaSegundos(),
                                "round", s.getRound(),
                                "timestamp", s.getTimestamp()
                        )).toList()
                ));

                // Calcular totales
                int totalAzulGolpes = statsAzul.stream().mapToInt(s -> s.getGolpesConectados() != null ? s.getGolpesConectados() : 0).sum();
                int totalRojoGolpes = statsRojo.stream().mapToInt(s -> s.getGolpesConectados() != null ? s.getGolpesConectados() : 0).sum();
                int totalAzulDerribos = statsAzul.stream().mapToInt(s -> s.getDerribos() != null ? s.getDerribos() : 0).sum();
                int totalRojoDerribos = statsRojo.stream().mapToInt(s -> s.getDerribos() != null ? s.getDerribos() : 0).sum();

                response.put("totalesCalculados", Map.of(
                        "azulGolpes", totalAzulGolpes,
                        "rojoGolpes", totalRojoGolpes,
                        "azulDerribos", totalAzulDerribos,
                        "rojoDerribos", totalRojoDerribos
                ));

                // Verificar probabilidades
                List<Probabilidad> todasProb = probabilidadRepository.findByPeleaIdOrderByTimestampDesc(peleaId);
                Probabilidad probAzul = probabilidadRepository
                        .findTopByPeleaIdAndLuchadorIdOrderByTimestampDesc(peleaId, primeraActiva.getAzul().getId());
                Probabilidad probRojo = probabilidadRepository
                        .findTopByPeleaIdAndLuchadorIdOrderByTimestampDesc(peleaId, primeraActiva.getRojo().getId());

                response.put("probabilidades", Map.of(
                        "total", todasProb.size(),
                        "azulActual", probAzul != null ? probAzul.getProbabilidad() : "null",
                        "rojoActual", probRojo != null ? probRojo.getProbabilidad() : "null"
                ));
            }

            // Estado del WebSocket
            response.put("webSocket", Map.of(
                    "clientesConectados", liveFightHandler.getConnectedClients(),
                    "peleaActual", liveFightHandler.getCurrentFightId()
            ));

            response.put("timestamp", LocalDateTime.now());
            response.put("status", "success");

        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "error");
        }

        return response;
    }

    /**
     * Crear datos de prueba para WebSocket
     */
    @PostMapping("/create-test-data/{peleaId}")
    public Map<String, Object> createTestData(@PathVariable Long peleaId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Pelea pelea = peleaRepository.findById(peleaId)
                    .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));

            // Crear estadísticas de prueba para ambos luchadores
            LocalDateTime now = LocalDateTime.now();

            // Luchador Azul - Estadísticas
            Estadistica statAzul = new Estadistica();
            statAzul.setPelea(pelea);
            statAzul.setLuchador(pelea.getAzul());
            statAzul.setRound(1);
            statAzul.setGolpesConectados(15);
            statAzul.setDerribos(2);
            statAzul.setControlJaulaSegundos(120);
            statAzul.setTimestamp(now.minusMinutes(5));
            estadisticaRepository.save(statAzul);

            // Luchador Rojo - Estadísticas
            Estadistica statRojo = new Estadistica();
            statRojo.setPelea(pelea);
            statRojo.setLuchador(pelea.getRojo());
            statRojo.setRound(1);
            statRojo.setGolpesConectados(12);
            statRojo.setDerribos(1);
            statRojo.setControlJaulaSegundos(80);
            statRojo.setTimestamp(now.minusMinutes(4));
            estadisticaRepository.save(statRojo);

            // Round 2 - Luchador Azul
            Estadistica statAzul2 = new Estadistica();
            statAzul2.setPelea(pelea);
            statAzul2.setLuchador(pelea.getAzul());
            statAzul2.setRound(2);
            statAzul2.setGolpesConectados(8);
            statAzul2.setDerribos(1);
            statAzul2.setControlJaulaSegundos(90);
            statAzul2.setTimestamp(now.minusMinutes(2));
            estadisticaRepository.save(statAzul2);

            // Round 2 - Luchador Rojo
            Estadistica statRojo2 = new Estadistica();
            statRojo2.setPelea(pelea);
            statRojo2.setLuchador(pelea.getRojo());
            statRojo2.setRound(2);
            statRojo2.setGolpesConectados(10);
            statRojo2.setDerribos(2);
            statRojo2.setControlJaulaSegundos(110);
            statRojo2.setTimestamp(now.minusMinutes(1));
            estadisticaRepository.save(statRojo2);

            // Crear probabilidades de prueba
            Probabilidad probAzul = new Probabilidad();
            probAzul.setPelea(pelea);
            probAzul.setLuchador(pelea.getAzul());
            probAzul.setProbabilidad(java.math.BigDecimal.valueOf(65.5));
            probAzul.setTimestamp(now.minusSeconds(30));
            probabilidadRepository.save(probAzul);

            Probabilidad probRojo = new Probabilidad();
            probRojo.setPelea(pelea);
            probRojo.setLuchador(pelea.getRojo());
            probRojo.setProbabilidad(java.math.BigDecimal.valueOf(34.5));
            probRojo.setTimestamp(now.minusSeconds(30));
            probabilidadRepository.save(probRojo);

            response.put("estadisticasCreadas", 4);
            response.put("probabilidadesCreadas", 2);
            response.put("totalesEsperados", Map.of(
                    "azulGolpes", 23,  // 15 + 8
                    "azulDerribos", 3, // 2 + 1
                    "azulControl", 210, // 120 + 90
                    "rojoGolpes", 22,  // 12 + 10
                    "rojoDerribos", 3, // 1 + 2
                    "rojoControl", 190, // 80 + 110
                    "azulProbabilidad", 65.5,
                    "rojoProbabilidad", 34.5
            ));
            response.put("status", "success");
            response.put("message", "Datos de prueba creados exitosamente");

            // Notificar al WebSocket de la actualización
            liveFightHandler.notifyDataUpdate(peleaId);

        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "error");
        }

        return response;
    }

    /**
     * Forzar actualización del WebSocket
     */
    @PostMapping("/force-websocket-update")
    public Map<String, Object> forceWebSocketUpdate() {
        Map<String, Object> response = new HashMap<>();

        try {
            Long currentFightId = liveFightHandler.getCurrentFightId();
            if (currentFightId != null) {
                liveFightHandler.notifyDataUpdate(currentFightId);
                response.put("message", "Actualización forzada enviada para pelea " + currentFightId);
            } else {
                response.put("message", "No hay pelea activa en el WebSocket");
            }

            response.put("clientesConectados", liveFightHandler.getConnectedClients());
            response.put("status", "success");

        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "error");
        }

        return response;
    }

    /**
     * Cambiar la pelea activa del WebSocket
     */
    @PostMapping("/set-active-fight/{peleaId}")
    public Map<String, Object> setActiveFight(@PathVariable Long peleaId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Pelea pelea = peleaRepository.findById(peleaId)
                    .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));

            liveFightHandler.setCurrentFightId(peleaId);

            response.put("message", "Pelea activa cambiada a: " + pelea.getEvento().getNombre() +
                    " - " + pelea.getAzul().getNombre() + " vs " + pelea.getRojo().getNombre());
            response.put("peleaId", peleaId);
            response.put("clientesConectados", liveFightHandler.getConnectedClients());
            response.put("status", "success");

        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "error");
        }

        return response;
    }

    /**
     * Limpiar todos los datos de prueba
     */
    @DeleteMapping("/clear-test-data/{peleaId}")
    public Map<String, Object> clearTestData(@PathVariable Long peleaId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Eliminar estadísticas de la pelea
            List<Estadistica> stats = estadisticaRepository.findByPeleaId(peleaId);
            estadisticaRepository.deleteAll(stats);

            // Eliminar probabilidades de la pelea
            List<Probabilidad> probs = probabilidadRepository.findByPeleaIdOrderByTimestampDesc(peleaId);
            probabilidadRepository.deleteAll(probs);

            response.put("estadisticasEliminadas", stats.size());
            response.put("probabilidadesEliminadas", probs.size());
            response.put("status", "success");
            response.put("message", "Datos de prueba eliminados exitosamente");

        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "error");
        }

        return response;
    }
}