package org.example.ufc_api.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchanges.user}")
    private String userExchange;

    @Value("${app.rabbitmq.exchanges.notification}")
    private String notificationExchange;

    @Value("${app.rabbitmq.queues.user-registration}")
    private String userRegistrationQueue;

    @Value("${app.rabbitmq.queues.email-notification}")
    private String emailNotificationQueue;

    @Value("${app.rabbitmq.routing-keys.user-registered}")
    private String userRegisteredRoutingKey;

    @Value("${app.rabbitmq.routing-keys.send-email}")
    private String sendEmailRoutingKey;

    // Exchanges
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(userExchange);
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(notificationExchange);
    }

    // Queues
    @Bean
    public Queue userRegistrationQueue() {
        return QueueBuilder.durable(userRegistrationQueue).build();
    }

    @Bean
    public Queue emailNotificationQueue() {
        return QueueBuilder.durable(emailNotificationQueue).build();
    }

    // Bindings
    @Bean
    public Binding userRegistrationBinding() {
        return BindingBuilder
                .bind(userRegistrationQueue())
                .to(userExchange())
                .with(userRegisteredRoutingKey);
    }

    @Bean
    public Binding emailNotificationBinding() {
        return BindingBuilder
                .bind(emailNotificationQueue())
                .to(notificationExchange())
                .with(sendEmailRoutingKey);
    }

    // Message Converter para JSON
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}