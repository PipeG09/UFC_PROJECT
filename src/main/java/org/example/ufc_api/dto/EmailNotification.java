package org.example.ufc_api.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EmailNotification {
    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> templateData;
    private LocalDateTime timestamp;
    private String eventType;

    // Constructores
    public EmailNotification() {
        this.templateData = new HashMap<>();
        this.timestamp = LocalDateTime.now();
    }

    public EmailNotification(String to, String subject, String templateName) {
        this();
        this.to = to;
        this.subject = subject;
        this.templateName = templateName;
    }

    // Método estático para crear email de bienvenida
    public static EmailNotification createWelcomeEmail(String correo, String nombre) {
        EmailNotification email = new EmailNotification();
        email.setTo(correo);
        email.setSubject("¡Bienvenido a UFC Live Tracker!");
        email.setTemplateName("welcome");
        email.setEventType("USER_REGISTRATION");

        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombre);
        data.put("correo", correo);
        email.setTemplateData(data);

        return email;
    }

    // Getters y Setters
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public Map<String, Object> getTemplateData() { return templateData; }
    public void setTemplateData(Map<String, Object> templateData) { this.templateData = templateData; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    @Override
    public String toString() {
        return "EmailNotification{" +
                "to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", templateName='" + templateName + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}