package com.doan1.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RabbitConfiguration {
    @Bean
    TopicExchange eventsExchange() { return new TopicExchange("doan1.events", true, false); }

    @Bean
    TopicExchange deadLetterExchange() { return new TopicExchange("doan1.events.dlx", true, false); }

    @Bean
    Queue packageNotificationsQueue() {
        return QueueBuilder.durable("notifications.catalog")
                .deadLetterExchange("doan1.events.dlx")
                .deadLetterRoutingKey("catalog.package.failed")
                .build();
    }

    @Bean
    Queue packageNotificationsDeadLetterQueue() { return QueueBuilder.durable("notifications.catalog.dlq").build(); }

    @Bean
    Queue bookingNotificationsQueue() {
        return QueueBuilder.durable("notifications.booking")
                .deadLetterExchange("doan1.events.dlx")
                .deadLetterRoutingKey("notification.booking.failed")
                .build();
    }

    @Bean
    Queue bookingNotificationsDeadLetterQueue() {
        return QueueBuilder.durable("notifications.booking.dlq").build();
    }

    @Bean
    Jackson2JsonMessageConverter rabbitJsonMessageConverter() { return new Jackson2JsonMessageConverter(); }

    @Bean
    Binding packageEventsBinding(Queue packageNotificationsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(packageNotificationsQueue).to(eventsExchange).with("catalog.package.#");
    }

    @Bean
    Binding packageDeadLetterBinding(Queue packageNotificationsDeadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(packageNotificationsDeadLetterQueue).to(deadLetterExchange).with("catalog.package.failed");
    }

    @Bean
    Binding bookingEventsBinding(Queue bookingNotificationsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(bookingNotificationsQueue).to(eventsExchange).with("booking.completed");
    }

    @Bean
    Binding bookingDeadLetterBinding(Queue bookingNotificationsDeadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(bookingNotificationsDeadLetterQueue)
                .to(deadLetterExchange).with("notification.booking.failed");
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitJsonMessageConverter());
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(retryAdvice());
        return factory;
    }

    @Bean
    RetryOperationsInterceptor retryAdvice() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(500, 2.0, 5000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }
}
