package org.example.ufc_api.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class LiveFightHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(LiveFightHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean isSimulationRunning = false;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("🔴 Cliente conectado: {} - Total clientes: {}",
                session.getId(), sessions.size());

        // Enviar mensaje de bienvenida
        sendToSession(session, new FightUpdateMessage("welcome", "Conectado a UFC Live Tracker"));

        // Iniciar simulación si es el primer cliente
        if (sessions.size() == 1 && !isSimulationRunning) {
            startLiveFightSimulation();
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        logger.info("📨 Mensaje recibido de {}: {}", session.getId(), message.getPayload());

        // Aquí puedes manejar mensajes del cliente
        // Por ejemplo: cambiar de pelea, pausar/reanudar, etc.
        String payload = (String) message.getPayload();

        if ("ping".equals(payload)) {
            sendToSession(session, new FightUpdateMessage("pong", "Conexión activa"));
        } else if ("request_update".equals(payload)) {
            // Enviar update inmediato
            FightUpdateMessage update = generateRandomFightUpdate();
            sendToSession(session, update);
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

        // Detener simulación si no hay clientes
        if (sessions.isEmpty() && isSimulationRunning) {
            stopLiveFightSimulation();
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
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
                    return true; // Remover sesión cerrada
                }
            } catch (IOException e) {
                logger.error("Error enviando mensaje a {}: {}", session.getId(), e.getMessage());
                return true; // Remover sesión con error
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

    // Simulación de pelea en vivo
    private void startLiveFightSimulation() {
        logger.info("🥊 Iniciando simulación de pelea en vivo");
        isSimulationRunning = true;

        scheduler.scheduleAtFixedRate(() -> {
            if (!sessions.isEmpty()) {
                FightUpdateMessage update = generateRandomFightUpdate();
                broadcastFightUpdate(update);
                logger.debug("📡 Broadcast enviado a {} clientes", sessions.size());
            }
        }, 0, 3, TimeUnit.SECONDS); // Actualizar cada 3 segundos
    }

    private void stopLiveFightSimulation() {
        logger.info("⏹️ Deteniendo simulación de pelea");
        isSimulationRunning = false;
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private FightUpdateMessage generateRandomFightUpdate() {
        // Generar estadísticas aleatorias realistas
        int blueStrikes = (int) (Math.random() * 15) + 5;
        int redStrikes = (int) (Math.random() * 15) + 5;
        int blueTakedowns = (int) (Math.random() * 3);
        int redTakedowns = (int) (Math.random() * 3);

        // Calcular probabilidades basadas en rendimiento
        double total = blueStrikes + redStrikes + blueTakedowns * 2 + redTakedowns * 2;
        double blueScore = blueStrikes + blueTakedowns * 2;
        int blueProbability = total > 0 ? (int) ((blueScore / total) * 100) : 50;
        int redProbability = 100 - blueProbability;

        // Generar tiempo aleatorio para el round
        int minutes = (int) (Math.random() * 5);
        int seconds = (int) (Math.random() * 60);
        String timeRemaining = String.format("%d:%02d", minutes, seconds);

        // Control de jaula en segundos
        int blueCageControl = (int) (Math.random() * 180);
        int redCageControl = (int) (Math.random() * 120);

        FightStats stats = new FightStats(
                blueStrikes, redStrikes,
                blueTakedowns, redTakedowns,
                blueCageControl, redCageControl,
                blueProbability, redProbability,
                (int) (Math.random() * 3) + 1, // Round actual (1-3)
                timeRemaining
        );

        return new FightUpdateMessage("fight-stats", stats);
    }

    // Método público para enviar actualizaciones desde otros servicios
    public void sendFightUpdate(FightStats stats) {
        FightUpdateMessage message = new FightUpdateMessage("fight-stats", stats);
        broadcastFightUpdate(message);
    }

    // Método para obtener número de clientes conectados
    public int getConnectedClients() {
        return sessions.size();
    }
}