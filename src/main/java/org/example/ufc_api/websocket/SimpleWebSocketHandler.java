package org.example.ufc_api.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Component
public class SimpleWebSocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SimpleWebSocketHandler.class);

    public SimpleWebSocketHandler() {
        logger.info("🔧 SimpleWebSocketHandler creado e inicializado");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("✅ WebSocket conectado exitosamente: {}", session.getId());
        session.sendMessage(new TextMessage("Conectado exitosamente a UFC Live Tracker"));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        logger.info("📨 Mensaje recibido: {}", message.getPayload());
        session.sendMessage(new TextMessage("Echo: " + message.getPayload()));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("❌ Error WebSocket: {}", exception.getMessage(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.info("🔌 WebSocket desconectado: {} - Status: {}", session.getId(), closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
