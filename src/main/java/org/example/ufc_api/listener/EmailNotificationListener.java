package org.example.ufc_api.listener;

import org.example.ufc_api.dto.EmailNotification;
import org.example.ufc_api.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationListener.class);

    private final EmailService emailService;

    public EmailNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Escucha notificaciones de email y las procesa
     */
    @RabbitListener(queues = "${app.rabbitmq.queues.email-notification}")
    public void handleEmailNotification(EmailNotification notification) {
        try {
            logger.info("🎧 Procesando notificación de email para: {}", notification.getTo());
            logger.info("📧 Detalles de la notificación: {}", notification);

            // Enviar el email
            emailService.sendEmail(notification);

            logger.info("✅ Email procesado y enviado exitosamente a: {}", notification.getTo());

        } catch (Exception e) {
            logger.error("❌ Error procesando notificación de email para {}: {}",
                    notification.getTo(), e.getMessage(), e);

            throw e; // Re-lanzar para que RabbitMQ maneje el reintento
        }
    }
}