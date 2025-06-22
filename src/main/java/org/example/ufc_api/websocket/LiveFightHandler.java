package org.example.ufc_api.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ufc_api.model.Pelea;
import org.example.ufc_api.model.Estadistica;
import org.example.ufc_api.repository.PeleaRepository;
import org.example.ufc_api.repository.EstadisticaRepository;
import org.example.ufc_api.repository.ProbabilidadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Handler para transmisión de peleas en vivo.
 */
@Component
public class LiveFightHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(LiveFightHandler.class);
    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private ScheduledExecutorService scheduler;
    private boolean isSimulationRunning = false;
    private Long currentFightId = null;

    @Autowired
    private ObjectMapper objectMapper;  // Configurado con JavaTimeModule

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
        // Si el scheduler está cerrado, recrearlo
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
            // Cambiar la pelea que se está transmitiendo
            String fightIdStr = payload.substring("select_fight:".length());
            currentFightId = Long.parseLong(fightIdStr);
            logger.info("🥊 Cambiando a pelea ID: {}", currentFightId);

            // Enviar datos inmediatos de la pelea seleccionada
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
        logger.info("🔌 Cliente desconectado: {} - Total clientes: {}",
                session.getId(), sessions.size());

        // Detener transmisión si no hay clientes
        if (sessions.isEmpty() && isSimulationRunning) {
            stopLiveFightTransmission();
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * Inicia la transmisión de datos en vivo desde la base de datos
     */
    private void startLiveFightTransmission() {
        logger.info("🥊 Iniciando transmisión de pelea en vivo");
        isSimulationRunning = true;

        List<Pelea> peleasActivas = peleaRepository.findByFinalizadaFalse();
        if (!peleasActivas.isEmpty()) {
            currentFightId = peleasActivas.get(0).getId();
            logger.info("📺 Transmitiendo pelea ID: {}", currentFightId);
        }

        // Programar actualización cada 2 segundos
        scheduler.scheduleAtFixedRate(() -> {
            if (!sessions.isEmpty() && currentFightId != null) {
                sendCurrentFightData();
            }
        }, 0, 2, TimeUnit.SECONDS);
    }


    /**
     * Envía los datos actuales de la pelea desde la base de datos
     */
    private void sendCurrentFightData() {
        try {
            if (currentFightId == null) {
                logger.warn("⚠️ No hay pelea seleccionada para transmitir");
                return;
            }

            // Obtener la pelea de la base de datos
            Pelea pelea = peleaRepository.findById(currentFightId).orElse(null);
            if (pelea == null || pelea.getFinalizada()) {
                logger.info("🏁 La pelea ha finalizado o no existe");
                // Buscar otra pelea activa
                List<Pelea> peleasActivas = peleaRepository.findByFinalizadaFalse();
                if (!peleasActivas.isEmpty()) {
                    currentFightId = peleasActivas.get(0).getId();
                    pelea = peleasActivas.get(0);
                } else {
                    // No hay peleas activas, enviar mensaje
                    broadcastFightUpdate(new FightUpdateMessage("no-fights",
                            "No hay peleas en vivo en este momento"));
                    return;
                }
            }

            // Obtener estadísticas más recientes de la base de datos
            List<Estadistica> statsAzul = estadisticaRepository
                    .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(
                            pelea.getId(), pelea.getAzul().getId());

            List<Estadistica> statsRojo = estadisticaRepository
                    .findByPeleaIdAndLuchadorIdOrderByTimestampDesc(
                            pelea.getId(), pelea.getRojo().getId());

            // Calcular totales
            int blueStrikes = statsAzul.stream()
                    .mapToInt(Estadistica::getGolpesConectados).sum();
            int redStrikes = statsRojo.stream()
                    .mapToInt(Estadistica::getGolpesConectados).sum();

            int blueTakedowns = statsAzul.stream()
                    .mapToInt(Estadistica::getDerribos).sum();
            int redTakedowns = statsRojo.stream()
                    .mapToInt(Estadistica::getDerribos).sum();

            int blueCageControl = statsAzul.stream()
                    .mapToInt(Estadistica::getControlJaulaSegundos).sum();
            int redCageControl = statsRojo.stream()
                    .mapToInt(Estadistica::getControlJaulaSegundos).sum();

            // Obtener probabilidades actuales
            var probAzul = probabilidadRepository
                    .findTopByPeleaIdAndLuchadorIdOrderByTimestampDesc(
                            pelea.getId(), pelea.getAzul().getId());
            var probRojo = probabilidadRepository
                    .findTopByPeleaIdAndLuchadorIdOrderByTimestampDesc(
                            pelea.getId(), pelea.getRojo().getId());

            int blueProbability = probAzul != null ?
                    probAzul.getProbabilidad().intValue() : 50;
            int redProbability = probRojo != null ?
                    probRojo.getProbabilidad().intValue() : 50;

            // Determinar round actual (basado en las estadísticas)
            int currentRound = statsAzul.isEmpty() ? 1 :
                    statsAzul.get(0).getRound();

            // Crear objeto con datos reales
            FightStats stats = new FightStats(
                    blueStrikes, redStrikes,
                    blueTakedowns, redTakedowns,
                    blueCageControl, redCageControl,
                    blueProbability, redProbability,
                    currentRound,
                    "5:00" // Esto podría calcularse basado en timestamps
            );

            // Agregar información de la pelea
            stats.setEventName(pelea.getEvento().getNombre());
            stats.setFightStatus(pelea.getFinalizada() ? "FINISHED" : "LIVE");

            // Enviar a todos los clientes
            FightUpdateMessage message = new FightUpdateMessage("fight-stats", stats);
            broadcastFightUpdate(message);

            logger.debug("📡 Datos actualizados enviados desde BD para pelea ID: {}",
                    currentFightId);

        } catch (Exception e) {
            logger.error("❌ Error obteniendo datos de la BD: {}", e.getMessage(), e);
        }
    }

    /**
     * Detiene la transmisión
     */
    private void stopLiveFightTransmission() {
        logger.info("⏹️ Deteniendo transmisión de pelea");
        isSimulationRunning = false;
        currentFightId = null;
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * Método público para actualizar datos cuando hay cambios en la BD
     * Puede ser llamado desde otros servicios cuando se actualicen estadísticas
     */
    public void notifyDataUpdate(Long peleaId) {
        if (peleaId.equals(currentFightId)) {
            logger.info("🔄 Notificación de actualización para pelea actual");
            sendCurrentFightData();
        }
    }

    // Método para enviar actualizaciones a todos los clientes
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
                logger.error("Error enviando mensaje a {}: {}", session.getId(), e.getMessage());
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
            logger.error("Error enviando mensaje a sesión: {}", e.getMessage());
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
        sendCurrentFightData();
    }
}