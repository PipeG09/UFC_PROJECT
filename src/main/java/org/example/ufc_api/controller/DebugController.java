package org.example.ufc_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/jackson-test")
    public Map<String, Object> testJacksonConfiguration() {
        Map<String, Object> response = new HashMap<>();

        // Información del ObjectMapper
        response.put("objectMapper", objectMapper.getClass().getName());
        response.put("writeDatesAsTimestamps",
                objectMapper.isEnabled(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));

        // Test de fecha
        LocalDateTime now = LocalDateTime.now();
        response.put("testDate", now);
        response.put("registeredModules", objectMapper.getRegisteredModuleIds());

        return response;
    }

    @GetMapping("/websocket-test")
    public Map<String, Object> testWebSocketConfig() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "WebSocket endpoint should be accessible at /live-fight");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}