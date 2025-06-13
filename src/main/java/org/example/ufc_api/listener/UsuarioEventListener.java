package org.example.ufc_api.listener;

import org.example.ufc_api.dto.EmailNotification;
import org.example.ufc_api.dto.UsuarioRegistroEvent;
import org.example.ufc_api.service.MessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioEventListener.class);

    private final MessagePublisher messagePublisher;

    public UsuarioEventListener(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    /**
     * Escucha eventos de registro de usuario y dispara el envío de email de bienvenida
     */
    @RabbitListener(queues = "${app.rabbitmq.queues.user-registration}")
    public void handleUserRegistration(UsuarioRegistroEvent event) {
        try {
            logger.info("🎧 Procesando evento de registro de usuario: {}", event.getCorreo());
            logger.info("📋 Detalles del evento: {}", event);

            // Crear notificación de email de bienvenida
            EmailNotification welcomeEmail = EmailNotification.createWelcomeEmail(
                    event.getCorreo(),
                    event.getNombre()
            );

            // Publicar la notificación de email
            messagePublisher.publishEmailNotification(welcomeEmail);

            // Aquí podrías agregar más lógica, como:
            // - Crear configuraciones por defecto para el usuario
            // - Registrar métricas de registro
            // - Enviar notificación a administradores
            // - Crear entrada en tabla de auditoría

            logger.info("✅ Evento de registro procesado exitosamente para: {}", event.getCorreo());

        } catch (Exception e) {
            logger.error("❌ Error procesando evento de registro para {}: {}",
                    event.getCorreo(), e.getMessage(), e);
            // Aquí podrías implementar lógica de reintento o envío a Dead Letter Queue
            throw e; // Re-lanzar para que RabbitMQ maneje el reintento
        }
    }
}