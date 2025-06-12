package org.example.ufc_api.service;

import org.example.ufc_api.dto.EmailNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    // Usar un valor por defecto si no está configurado
    @Value("${spring.mail.username:noreply@ufctracker.com}")
    private String fromEmail;

    @Autowired(required = false)
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía email usando la información de la notificación
     */
    public void sendEmail(EmailNotification notification) {
        try {
            logger.info("📧 Preparando envío de email a: {}", notification.getTo());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(notification.getTo());
            helper.setSubject(notification.getSubject());

            // Generar contenido HTML basado en el template
            String htmlContent = generateEmailContent(notification);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            logger.info("✅ Email enviado exitosamente a: {}", notification.getTo());

        } catch (Exception e) {
            logger.error("❌ Error enviando email a {}: {}", notification.getTo(), e.getMessage(), e);
            throw new RuntimeException("Error enviando email", e);
        }
    }

    /**
     * Genera el contenido HTML del email basado en el template
     */
    private String generateEmailContent(EmailNotification notification) {
        return switch (notification.getTemplateName()) {
            case "welcome" -> generateWelcomeEmailTemplate(notification.getTemplateData());
            default -> generateDefaultTemplate(notification);
        };
    }

    /**
     * Template para email de bienvenida
     */
    private String generateWelcomeEmailTemplate(Map<String, Object> data) {
        String nombre = (String) data.get("nombre");
        String correo = (String) data.get("correo");

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Bienvenido a UFC Live Tracker</title>
                <style>
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        max-width: 600px; 
                        margin: 0 auto; 
                        padding: 20px; 
                        background-color: #f5f5f5;
                    }
                    .container {
                        background: white;
                        border-radius: 10px;
                        overflow: hidden;
                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                    }
                    .header { 
                        background: linear-gradient(135deg, #d32f2f, #f57c00); 
                        color: white; 
                        padding: 40px; 
                        text-align: center; 
                    }
                    .content { 
                        padding: 30px; 
                    }
                    .welcome-title { 
                        font-size: 28px; 
                        margin: 0; 
                        text-shadow: 2px 2px 4px rgba(0,0,0,0.3); 
                    }
                    .subtitle { 
                        font-size: 16px; 
                        margin: 10px 0 0 0; 
                        opacity: 0.9; 
                    }
                    .main-text { 
                        font-size: 16px; 
                        margin: 20px 0; 
                    }
                    .features { 
                        background: #f8f9fa; 
                        padding: 20px; 
                        border-radius: 8px; 
                        margin: 20px 0; 
                        border-left: 4px solid #d32f2f; 
                    }
                    .feature-item { 
                        margin: 10px 0; 
                        padding: 5px 0;
                    }
                    .cta-button { 
                        display: inline-block; 
                        background: #d32f2f; 
                        color: white; 
                        padding: 15px 30px; 
                        text-decoration: none; 
                        border-radius: 5px; 
                        font-weight: bold; 
                        margin: 20px 0; 
                        transition: background-color 0.3s;
                    }
                    .cta-button:hover {
                        background: #b71c1c;
                    }
                    .footer { 
                        text-align: center; 
                        margin-top: 30px; 
                        padding-top: 20px; 
                        border-top: 1px solid #ddd; 
                        color: #666; 
                        font-size: 14px; 
                    }
                    .highlight {
                        background: #fff3e0;
                        padding: 2px 6px;
                        border-radius: 4px;
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1 class="welcome-title">🥊 ¡Bienvenido a UFC Live Tracker!</h1>
                        <p class="subtitle">Tu plataforma definitiva para seguimiento en tiempo real de peleas UFC</p>
                    </div>
                    
                    <div class="content">
                        <p class="main-text">¡Hola <strong>%s</strong>!</p>
                        
                        <p class="main-text">
                            Nos emociona tenerte como parte de la comunidad de <strong>UFC Live Tracker</strong>. 
                            Ahora podrás disfrutar de la experiencia más completa para seguir las peleas de UFC en tiempo real.
                        </p>
                        
                        <div class="features">
                            <h3>🎯 ¿Qué puedes hacer ahora?</h3>
                            <div class="feature-item">📊 <strong>Estadísticas en tiempo real</strong> - Sigue cada golpe, derribo y control de jaula</div>
                            <div class="feature-item">🎲 <strong>Probabilidades dinámicas</strong> - Mira cómo cambian las chances de victoria</div>
                            <div class="feature-item">⚡ <strong>Notificaciones instantáneas</strong> - No te pierdas ningún KO o submission</div>
                            <div class="feature-item">📈 <strong>Análisis detallado</strong> - Profundiza en el rendimiento de cada peleador</div>
                            <div class="feature-item">🔍 <strong>Gestión de eventos</strong> - Crea y administra eventos de UFC</div>
                        </div>
                        
                        <p class="main-text">
                            Tu cuenta ha sido creada exitosamente con el email: <span class="highlight">%s</span>
                        </p>
                        
                        <center>
                            <a href="http://localhost:8080/api/usuarios" class="cta-button">🚀 Comenzar a Usar la API</a>
                        </center>
                        
                        <p class="main-text">
                            Si tienes alguna pregunta o necesitas ayuda, no dudes en contactarnos. 
                            ¡Estamos aquí para hacer que tu experiencia sea increíble!
                        </p>
                        
                        <div style="background: #e3f2fd; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <strong>💡 Tip:</strong> Explora nuestros endpoints para gestionar luchadores, peleas, estadísticas y probabilidades en tiempo real.
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>📧 Este email fue enviado automáticamente por UFC Live Tracker</p>
                        <p>🌐 API Base URL: <a href="http://localhost:8080/api">http://localhost:8080/api</a></p>
                        <p><small>©️ 2025 UFC Live Tracker. Todos los derechos reservados.</small></p>
                    </div>
                </div>
            </body>
            </html>
            """, nombre, correo);
    }

    /**
     * Template por defecto para otros tipos de email
     */
    private String generateDefaultTemplate(EmailNotification notification) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>%s</title>
                <style>
                    body { 
                        font-family: Arial, sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        max-width: 600px; 
                        margin: 0 auto; 
                        padding: 20px; 
                    }
                    .header {
                        background: #d32f2f;
                        color: white;
                        padding: 20px;
                        text-align: center;
                        border-radius: 5px 5px 0 0;
                    }
                    .content {
                        background: #f9f9f9;
                        padding: 20px;
                        border-radius: 0 0 5px 5px;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h2>🥊 UFC Live Tracker</h2>
                </div>
                <div class="content">
                    <h3>%s</h3>
                    <p>Tienes una nueva notificación de UFC Live Tracker.</p>
                    <p><small>Este es un email automático del sistema.</small></p>
                </div>
            </body>
            </html>
            """, notification.getSubject(), notification.getSubject());
    }
}