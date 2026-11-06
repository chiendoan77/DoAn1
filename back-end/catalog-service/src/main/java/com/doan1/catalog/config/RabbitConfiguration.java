package com.doan1.catalog.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean
    TopicExchange catalogEventsExchange() {
        return new TopicExchange("doan1.events", true, false);
    }
}
