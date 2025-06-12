package org.example.ufc_api.service;

import org.example.ufc_api.dto.EmailNotification;
import org.example.ufc_api.dto.UsuarioRegistroEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisher {

    private static final Logger logger = LoggerFactory.getLogger(MessagePublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchanges.user}")
    private String userExchange;

    @Value("${app.rabbitmq.exchanges.notification}")
    private String notificationExchange;

    @Value("${app.rabbitmq.routing-keys.user-registered}")
    private String userRegisteredRoutingKey;

    @Value("${app.rabbitmq.routing-keys.send-email}")
    private String sendEmailRoutingKey;

    public MessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publica evento de registro de usuario
     */
    public void publishUserRegistration(UsuarioRegistroEvent event) {
        try {
            logger.info("📡 Publicando evento de registro de usuario: {}", event.getCorreo());

            rabbitTemplate.convertAndSend(
                    userExchange,
                    userRegisteredRoutingKey,
                    event
            );

            logger.info("✅ Evento de registro publicado exitosamente para usuario: {}", event.getNombre());
        } catch (Exception e) {
            logger.error("❌ Error publicando evento de registro para usuario {}: {}",
                    event.getNombre(), e.getMessage(), e);
        }
    }

    /**
     * Publica notificación de email
     */
    public void publishEmailNotification(EmailNotification notification) {
        try {
            logger.info("📧 Publicando notificación de email para: {}", notification.getTo());

            rabbitTemplate.convertAndSend(
                    notificationExchange,
                    sendEmailRoutingKey,
                    notification
            );

            logger.info("✅ Notificación de email publicada exitosamente para: {}", notification.getTo());
        } catch (Exception e) {
            logger.error("❌ Error publicando notificación de email para {}: {}",
                    notification.getTo(), e.getMessage(), e);
        }
    }

    /**
     * Método de conveniencia para enviar email de bienvenida
     */
    public void sendWelcomeEmail(String correo, String nombre) {
        EmailNotification welcomeEmail = EmailNotification.createWelcomeEmail(correo, nombre);
        publishEmailNotification(welcomeEmail);
    }
}