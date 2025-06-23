package org.example.ufc_api.controller;

import org.example.ufc_api.model.Estadistica;
import org.example.ufc_api.model.Pelea;
import org.example.ufc_api.repository.EstadisticaRepository;
import org.example.ufc_api.repository.PeleaRepository;
import org.example.ufc_api.websocket.LiveFightHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fight-control")
public class FightSwitchController {

    private final PeleaRepository peleaRepository;
    private final EstadisticaRepository estadisticaRepository;
    private final LiveFightHandler liveFightHandler;

    // Constructor injection para mejor manejo de dependencias
    @Autowired
    public FightSwitchController(PeleaRepository peleaRepository,
                                 EstadisticaRepository estadisticaRepository,
                                 LiveFightHandler liveFightHandler) {
        this.peleaRepository = peleaRepository;
        this.estadisticaRepository = estadisticaRepository;
        this.liveFightHandler = liveFightHandler;
    }

    /**
     * Buscar pelea con más datos y cambiar WebSocket a esa pelea
     */
    @PostMapping("/switch-to-best-fight")
    public Map<String, Object> switchToBestFight() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Obtener todas las peleas activas
            List<Pelea> peleasActivas = peleaRepository.findByFinalizadaFalse();

            Long mejorPeleaId = null;
            int maxEstadisticas = 0;
            Map<String, Object> mejorPeleaInfo = null;

            // Evaluar cada pelea activa
            for (Pelea pelea : peleasActivas) {
                List<Estadistica> stats = estadisticaRepository.findByPeleaId(pelea.getId());

                if (stats.size() > maxEstadisticas) {
                    maxEstadisticas = stats.size();
                    mejorPeleaId = pelea.getId();

                    // Calcular totales para mostrar
                    List<Estadistica> statsAzul = estadisticaRepository
                            .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(pelea.getId(), pelea.getAzul().getId());
                    List<Estadistica> statsRojo = estadisticaRepository
                            .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(pelea.getId(), pelea.getRojo().getId());

                    int totalAzulGolpes = statsAzul.stream()
                            .mapToInt(s -> s.getGolpesConectados() != null ? s.getGolpesConectados() : 0)
                            .sum();
                    int totalRojoGolpes = statsRojo.stream()
                            .mapToInt(s -> s.getGolpesConectados() != null ? s.getGolpesConectados() : 0)
                            .sum();

                    mejorPeleaInfo = new HashMap<>();
                    mejorPeleaInfo.put("id", pelea.getId());
                    mejorPeleaInfo.put("evento", pelea.getEvento().getNombre());
                    mejorPeleaInfo.put("azul", pelea.getAzul().getNombre());
                    mejorPeleaInfo.put("rojo", pelea.getRojo().getNombre());
                    mejorPeleaInfo.put("totalEstadisticas", stats.size());
                    mejorPeleaInfo.put("azulGolpes", totalAzulGolpes);
                    mejorPeleaInfo.put("rojoGolpes", totalRojoGolpes);
                }
            }

            if (mejorPeleaId != null) {
                // Obtener ID actual antes del cambio
                Long peleaAnterior = null;
                try {
                    peleaAnterior = liveFightHandler.getCurrentFightId();
                } catch (Exception e) {
                    // Si hay error obteniendo el ID actual, continuar
                }

                // Cambiar WebSocket a la mejor pelea
                liveFightHandler.setCurrentFightId(mejorPeleaId);

                response.put("status", "success");
                response.put("message", "WebSocket cambiado a la pelea con más datos");
                response.put("peleaAnterior", peleaAnterior != null ? peleaAnterior : "ninguna");
                response.put("nuevaPelea", mejorPeleaInfo);

                try {
                    response.put("clientesConectados", liveFightHandler.getConnectedClients());
                } catch (Exception e) {
                    response.put("clientesConectados", "error_obteniendo");
                }
            } else {
                response.put("status", "warning");
                response.put("message", "No se encontraron peleas activas con estadísticas");
                response.put("peleasEvaluadas", peleasActivas.size());
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
            response.put("stackTrace", e.getClass().getSimpleName());
        }

        return response;
    }

    /**
     * Listar todas las peleas con sus estadísticas
     */
    @GetMapping("/list-fights-with-stats")
    public Map<String, Object> listFightsWithStats() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Pelea> peleasActivas = peleaRepository.findByFinalizadaFalse();

            List<Map<String, Object>> peleasConStats = peleasActivas.stream()
                    .map(pelea -> {
                        List<Estadistica> stats = estadisticaRepository.findByPeleaId(pelea.getId());

                        List<Estadistica> statsAzul = estadisticaRepository
                                .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(pelea.getId(), pelea.getAzul().getId());
                        List<Estadistica> statsRojo = estadisticaRepository
                                .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(pelea.getId(), pelea.getRojo().getId());

                        int totalAzulGolpes = statsAzul.stream()
                                .mapToInt(s -> s.getGolpesConectados() != null ? s.getGolpesConectados() : 0)
                                .sum();
                        int totalRojoGolpes = statsRojo.stream()
                                .mapToInt(s -> s.getGolpesConectados() != null ? s.getGolpesConectados() : 0)
                                .sum();

                        boolean esActual = false;
                        try {
                            Long currentId = liveFightHandler.getCurrentFightId();
                            esActual = currentId != null && currentId.equals(pelea.getId());
                        } catch (Exception e) {
                            // Si hay error, asumir que no es actual
                        }

                        Map<String, Object> peleaMap = new HashMap<>();
                        peleaMap.put("id", pelea.getId());
                        peleaMap.put("evento", pelea.getEvento().getNombre());
                        peleaMap.put("azul", pelea.getAzul().getNombre());
                        peleaMap.put("rojo", pelea.getRojo().getNombre());
                        peleaMap.put("fecha", pelea.getFecha());
                        peleaMap.put("totalEstadisticas", stats.size());
                        peleaMap.put("azulStats", statsAzul.size());
                        peleaMap.put("rojoStats", statsRojo.size());
                        peleaMap.put("azulGolpes", totalAzulGolpes);
                        peleaMap.put("rojoGolpes", totalRojoGolpes);
                        peleaMap.put("esActual", esActual);

                        return peleaMap;
                    })
                    .toList();

            response.put("peleas", peleasConStats);
            response.put("totalPeleas", peleasActivas.size());

            try {
                response.put("peleaActualWebSocket", liveFightHandler.getCurrentFightId());
                response.put("clientesConectados", liveFightHandler.getConnectedClients());
            } catch (Exception e) {
                response.put("peleaActualWebSocket", "error_obteniendo");
                response.put("clientesConectados", "error_obteniendo");
            }

            response.put("status", "success");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
        }

        return response;
    }

    /**
     * Cambiar manualmente a una pelea específica
     */
    @PostMapping("/switch-to-fight/{peleaId}")
    public Map<String, Object> switchToSpecificFight(@PathVariable Long peleaId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Pelea pelea = peleaRepository.findById(peleaId)
                    .orElseThrow(() -> new RuntimeException("Pelea no encontrada"));

            // Obtener estadísticas para mostrar info
            List<Estadistica> stats = estadisticaRepository.findByPeleaId(peleaId);

            liveFightHandler.setCurrentFightId(peleaId);

            response.put("status", "success");
            response.put("message", "WebSocket cambiado a pelea específica");
            Map<String, Object> peleaInfo = new HashMap<>();
            peleaInfo.put("id", pelea.getId());
            peleaInfo.put("evento", pelea.getEvento().getNombre());
            peleaInfo.put("azul", pelea.getAzul().getNombre());
            peleaInfo.put("rojo", pelea.getRojo().getNombre());
            peleaInfo.put("totalEstadisticas", stats.size());

            response.put("pelea", peleaInfo);

            try {
                response.put("clientesConectados", liveFightHandler.getConnectedClients());
            } catch (Exception e) {
                response.put("clientesConectados", "error_obteniendo");
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
        }

        return response;
    }

    /**
     * Endpoint de prueba para verificar que el controlador funciona
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put("status", "ok");
            response.put("message", "FightSwitchController funcionando correctamente");
            response.put("timestamp", java.time.LocalDateTime.now());

            // Verificar conexiones básicas
            long totalPeleas = peleaRepository.count();
            long totalEstadisticas = estadisticaRepository.count();

            response.put("totalPeleasEnBD", totalPeleas);
            response.put("totalEstadisticasEnBD", totalEstadisticas);

            try {
                Long currentFight = liveFightHandler.getCurrentFightId();
                int clients = liveFightHandler.getConnectedClients();
                response.put("webSocketActivo", true);
                response.put("peleaActual", currentFight);
                response.put("clientesConectados", clients);
            } catch (Exception e) {
                response.put("webSocketActivo", false);
                response.put("webSocketError", e.getMessage());
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
        }

        return response;
    }
}