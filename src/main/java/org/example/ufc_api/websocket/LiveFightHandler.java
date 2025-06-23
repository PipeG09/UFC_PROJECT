package org.example.ufc_api.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ufc_api.model.Pelea;
import org.example.ufc_api.model.Estadistica;
import org.example.ufc_api.model.Probabilidad;
import org.example.ufc_api.repository.PeleaRepository;
import org.example.ufc_api.repository.EstadisticaRepository;
import org.example.ufc_api.repository.ProbabilidadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Handler para transmisión de peleas en vivo - FIXED VERSION
 */
@Component
public class LiveFightHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(LiveFightHandler.class);
    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private ScheduledExecutorService scheduler;
    private boolean isSimulationRunning = false;
    private Long currentFightId = null;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PeleaRepository peleaRepository;

    @Autowired
    private EstadisticaRepository estadisticaRepository;

    @Autowired
    private ProbabilidadRepository probabilidadRepository;

    public LiveFightHandler() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        sessions.add(session);
        logger.info("🔴 Cliente conectado: {} - Total clientes: {}", session.getId(), sessions.size());
        sendToSession(session, new FightUpdateMessage("welcome", "Conectado a UFC Live Tracker"));

        if (sessions.size() == 1 && !isSimulationRunning) {
            startLiveFightTransmission();
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        logger.info("📨 Mensaje recibido de {}: {}", session.getId(), message.getPayload());

        String payload = (String) message.getPayload();

        if ("ping".equals(payload)) {
            sendToSession(session, new FightUpdateMessage("pong", "Conexión activa"));
        } else if (payload.startsWith("select_fight:")) {
            String fightIdStr = payload.substring("select_fight:".length());
            currentFightId = Long.parseLong(fightIdStr);
            logger.info("🥊 Cambiando a pelea ID: {}", currentFightId);
            sendCurrentFightData();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("❌ Error en sesión {}: {}", session.getId(), exception.getMessage());
        sessions.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        logger.info("🔌 Cliente desconectado: {} - Total clientes: {}", session.getId(), sessions.size());

        if (sessions.isEmpty() && isSimulationRunning) {
            stopLiveFightTransmission();
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 🔧 FIXED: Buscar pelea con datos más recientes
     */
    private void startLiveFightTransmission() {
        logger.info("🥊 Iniciando transmisión de pelea en vivo");
        isSimulationRunning = true;

        // 🔧 NUEVA LÓGICA: Buscar pelea activa que tenga estadísticas
        currentFightId = findBestActiveFight();

        if (currentFightId != null) {
            logger.info("📺 Transmitiendo pelea ID: {}", currentFightId);
        } else {
            logger.warn("⚠️ No hay peleas con datos para transmitir");
        }

        // Programar actualización cada 3 segundos
        scheduler.scheduleAtFixedRate(() -> {
            if (!sessions.isEmpty()) {
                sendCurrentFightData();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    /**
     * 🔧 NUEVO: Buscar la mejor pelea activa (que tenga estadísticas)
     */
    private Long findBestActiveFight() {
        try {
            // Obtener todas las peleas activas
            List<Pelea> peleasActivas = peleaRepository.findByFinalizadaFalse();
            logger.info("🔍 Evaluando {} peleas activas para encontrar la mejor", peleasActivas.size());

            // Buscar pelea con más estadísticas recientes
            Long mejorPeleaId = null;
            int maxEstadisticas = 0;

            for (Pelea pelea : peleasActivas) {
                List<Estadistica> stats = estadisticaRepository.findByPeleaId(pelea.getId());
                logger.debug("📊 Pelea {} tiene {} estadísticas", pelea.getId(), stats.size());

                if (stats.size() > maxEstadisticas) {
                    maxEstadisticas = stats.size();
                    mejorPeleaId = pelea.getId();
                    logger.info("🎯 Nueva mejor pelea: ID {} con {} estadísticas", mejorPeleaId, maxEstadisticas);
                }
            }

            // Si no hay peleas con estadísticas, tomar la primera disponible
            if (mejorPeleaId == null && !peleasActivas.isEmpty()) {
                mejorPeleaId = peleasActivas.get(0).getId();
                logger.info("📌 Usando primera pelea disponible: ID {}", mejorPeleaId);
            }

            return mejorPeleaId;

        } catch (Exception e) {
            logger.error("❌ Error buscando mejor pelea activa: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 🔧 FIXED: Envía los datos actuales con logs detallados
     */
    private void sendCurrentFightData() {
        try {
            if (currentFightId == null) {
                currentFightId = findBestActiveFight();
                if (currentFightId == null) {
                    logger.warn("⚠️ No hay pelea seleccionada y no hay peleas disponibles");
                    broadcastFightUpdate(new FightUpdateMessage("no-fights",
                            "No hay peleas en vivo en este momento"));
                    return;
                }
            }

            // Obtener la pelea
            Pelea pelea = peleaRepository.findById(currentFightId).orElse(null);
            if (pelea == null) {
                logger.error("❌ Pelea con ID {} no encontrada", currentFightId);
                currentFightId = findBestActiveFight();
                return;
            }

            logger.debug("🥊 Procesando pelea ID: {} - {} vs {}",
                    currentFightId, pelea.getAzul().getNombre(), pelea.getRojo().getNombre());

            if (pelea.getFinalizada()) {
                logger.info("🏁 La pelea {} ha finalizado, buscando otra...", currentFightId);
                currentFightId = findBestActiveFight();
                if (currentFightId == null) {
                    broadcastFightUpdate(new FightUpdateMessage("no-fights",
                            "No hay peleas en vivo en este momento"));
                    return;
                }
                // Recursión para procesar la nueva pelea
                sendCurrentFightData();
                return;
            }

            // 🔧 LOGS DETALLADOS: Obtener estadísticas
            Long azulId = pelea.getAzul().getId();
            Long rojoId = pelea.getRojo().getId();

            logger.debug("🔍 Buscando estadísticas para pelea {} - AzulID: {}, RojoID: {}",
                    currentFightId, azulId, rojoId);

            List<Estadistica> statsAzul = estadisticaRepository
                    .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(currentFightId, azulId);

            List<Estadistica> statsRojo = estadisticaRepository
                    .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(currentFightId, rojoId);

            logger.info("📈 Estadísticas encontradas - Pelea {}: Azul[{}] = {}, Rojo[{}] = {}",
                    currentFightId, azulId, statsAzul.size(), rojoId, statsRojo.size());

            // Log de las estadísticas individuales para debug
            if (!statsAzul.isEmpty()) {
                Estadistica ultimaAzul = statsAzul.get(0);
                logger.debug("🔵 Última stat azul: {} golpes, {} derribos, {} control",
                        ultimaAzul.getGolpesConectados(), ultimaAzul.getDerribos(), ultimaAzul.getControlJaulaSegundos());
            }

            if (!statsRojo.isEmpty()) {
                Estadistica ultimaRoja = statsRojo.get(0);
                logger.debug("🔴 Última stat roja: {} golpes, {} derribos, {} control",
                        ultimaRoja.getGolpesConectados(), ultimaRoja.getDerribos(), ultimaRoja.getControlJaulaSegundos());
            }

            // 🔧 CÁLCULO CORRECTO: Sumar estadísticas
            int blueStrikes = statsAzul.stream()
                    .mapToInt(s -> s.getGolpesConectados() != null ? s.getGolpesConectados() : 0)
                    .sum();

            int redStrikes = statsRojo.stream()
                    .mapToInt(s -> s.getGolpesConectados() != null ? s.getGolpesConectados() : 0)
                    .sum();

            int blueTakedowns = statsAzul.stream()
                    .mapToInt(s -> s.getDerribos() != null ? s.getDerribos() : 0)
                    .sum();

            int redTakedowns = statsRojo.stream()
                    .mapToInt(s -> s.getDerribos() != null ? s.getDerribos() : 0)
                    .sum();

            int blueCageControl = statsAzul.stream()
                    .mapToInt(s -> s.getControlJaulaSegundos() != null ? s.getControlJaulaSegundos() : 0)
                    .sum();

            int redCageControl = statsRojo.stream()
                    .mapToInt(s -> s.getControlJaulaSegundos() != null ? s.getControlJaulaSegundos() : 0)
                    .sum();

            // Log de totales calculados
            logger.info("🧮 Totales calculados - Azul: {}🥊 {}🤼 {}⏱ | Rojo: {}🥊 {}🤼 {}⏱",
                    blueStrikes, blueTakedowns, blueCageControl,
                    redStrikes, redTakedowns, redCageControl);

            // Obtener probabilidades
            Probabilidad probAzul = probabilidadRepository
                    .findTopByPeleaIdAndLuchadorIdOrderByTimestampDesc(currentFightId, azulId);

            Probabilidad probRojo = probabilidadRepository
                    .findTopByPeleaIdAndLuchadorIdOrderByTimestampDesc(currentFightId, rojoId);

            int blueProbability = 50;
            int redProbability = 50;

            if (probAzul != null && probRojo != null) {
                blueProbability = probAzul.getProbabilidad().intValue();
                redProbability = probRojo.getProbabilidad().intValue();
                logger.debug("💯 Probabilidades de BD - Azul: {}%, Rojo: {}%", blueProbability, redProbability);
            } else {
                // Calcular probabilidades basadas en estadísticas
                int totalActivity = blueStrikes + redStrikes + blueTakedowns + redTakedowns;
                if (totalActivity > 0) {
                    int blueActivity = blueStrikes + (blueTakedowns * 3) + (blueCageControl / 30);
                    int redActivity = redStrikes + (redTakedowns * 3) + (redCageControl / 30);
                    int totalCalc = blueActivity + redActivity;

                    if (totalCalc > 0) {
                        blueProbability = Math.min(80, Math.max(20, (blueActivity * 100) / totalCalc));
                        redProbability = 100 - blueProbability;
                    }
                }
                logger.debug("🧮 Probabilidades calculadas - Azul: {}%, Rojo: {}%", blueProbability, redProbability);
            }

            // Determinar round actual
            int currentRound = 1;
            if (!statsAzul.isEmpty()) {
                currentRound = Math.max(currentRound,
                        statsAzul.get(0).getRound() != null ? statsAzul.get(0).getRound() : 1);
            }
            if (!statsRojo.isEmpty()) {
                currentRound = Math.max(currentRound,
                        statsRojo.get(0).getRound() != null ? statsRojo.get(0).getRound() : 1);
            }

            // Crear y enviar datos
            FightStats stats = new FightStats(
                    blueStrikes, redStrikes,
                    blueTakedowns, redTakedowns,
                    blueCageControl, redCageControl,
                    blueProbability, redProbability,
                    currentRound,
                    "5:00"
            );

            stats.setEventName(pelea.getEvento().getNombre());
            stats.setFightStatus(pelea.getFinalizada() ? "FINISHED" : "LIVE");

            // 🔧 LOG FINAL: Datos que se van a enviar
            logger.info("📡 ENVIANDO: Pelea {} - Azul: {}🥊 {}🤼 {}⏱ {}% | Rojo: {}🥊 {}🤼 {}⏱ {}%",
                    currentFightId,
                    blueStrikes, blueTakedowns, blueCageControl, blueProbability,
                    redStrikes, redTakedowns, redCageControl, redProbability);

            FightUpdateMessage message = new FightUpdateMessage("fight-stats", stats);
            broadcastFightUpdate(message);

        } catch (Exception e) {
            logger.error("❌ Error obteniendo datos de la BD para pelea {}: {}", currentFightId, e.getMessage(), e);
        }
    }

    private void stopLiveFightTransmission() {
        logger.info("⏹️ Deteniendo transmisión de pelea");
        isSimulationRunning = false;
        currentFightId = null;
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public void notifyDataUpdate(Long peleaId) {
        if (peleaId != null && peleaId.equals(currentFightId)) {
            logger.info("🔄 Notificación de actualización para pelea actual: {}", peleaId);
            sendCurrentFightData();
        }
    }

    public void broadcastFightUpdate(FightUpdateMessage update) {
        sessions.removeIf(session -> {
            try {
                if (session.isOpen()) {
                    String json = objectMapper.writeValueAsString(update);
                    session.sendMessage(new TextMessage(json));
                    return false;
                } else {
                    return true;
                }
            } catch (IOException e) {
                logger.error("❌ Error enviando mensaje a {}: {}", session.getId(), e.getMessage());
                return true;
            }
        });
    }

    private void sendToSession(WebSocketSession session, FightUpdateMessage message) {
        try {
            if (session.isOpen()) {
                String json = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(json));
            }
        } catch (IOException e) {
            logger.error("❌ Error enviando mensaje a sesión: {}", e.getMessage());
        }
    }

    // Getters útiles
    public int getConnectedClients() {
        return sessions.size();
    }

    public Long getCurrentFightId() {
        return currentFightId;
    }

    public void setCurrentFightId(Long fightId) {
        this.currentFightId = fightId;
        logger.info("🎯 Cambiando pelea actual a ID: {}", fightId);
        sendCurrentFightData();
    }
}