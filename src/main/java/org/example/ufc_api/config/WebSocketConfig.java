package org.example.ufc_api.config;

import org.example.ufc_api.websocket.LiveFightHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new LiveFightHandler(), "/live-fight")
                .setAllowedOrigins(
                        "http://localhost:8080",     // Servidor Spring Boot
                        "http://127.0.0.1:8080",     // IP local alternativa
                        "http://localhost:3000",     // Si usas servidor de desarrollo separado
                        "http://127.0.0.1:3000"      // IP local para servidor dev
                )
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*") // Cualquier puerto local
                .withSockJS();                   // Fallback para navegadores antiguos
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}