package com.doan1.booking.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean
    TopicExchange bookingEventsExchange() {
        return new TopicExchange("doan1.events", true, false);
    }
}
