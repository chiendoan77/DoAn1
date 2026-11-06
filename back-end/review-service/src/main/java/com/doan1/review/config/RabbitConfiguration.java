package com.doan1.review.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean
    TopicExchange reviewEventsExchange() {
        return new TopicExchange("doan1.events", true, false);
    }
}
