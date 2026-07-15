package com.petlife.modules.notification.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "notification.queue";
    public static final String EXCHANGE_NAME = "notification.exchange";
    public static final String ROUTING_KEY = "notification.routingKey";

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(ROUTING_KEY);
    }

    @Bean
    @SuppressWarnings("deprecation")
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
