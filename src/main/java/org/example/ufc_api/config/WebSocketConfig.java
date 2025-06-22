package org.example.ufc_api.config;

import org.example.ufc_api.websocket.LiveFightHandler;
import org.example.ufc_api.websocket.SimpleWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Bean
    public LiveFightHandler liveFightHandler() {
        logger.info("🔧 Creando LiveFightHandler bean");
        return new LiveFightHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        logger.info("🔧 Registrando LiveFightHandler en /live-fight");

        registry.addHandler(liveFightHandler(), "/live-fight")
                .setAllowedOriginPatterns("*");

        logger.info("✅ LiveFightHandler registrado exitosamente");
    }
}