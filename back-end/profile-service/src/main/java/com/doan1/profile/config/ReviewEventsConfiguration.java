package com.doan1.profile.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class ReviewEventsConfiguration {
    @Bean
    TopicExchange domainEventsExchange() {
        return new TopicExchange("doan1.events", true, false);
    }

    @Bean
    TopicExchange profileDeadLetterExchange() {
        return new TopicExchange("doan1.events.dlx", true, false);
    }

    @Bean
    Queue profileReviewEventsQueue() {
        return QueueBuilder.durable("profile.reviews")
                .deadLetterExchange("doan1.events.dlx")
                .deadLetterRoutingKey("profile.review.failed")
                .build();
    }

    @Bean
    Queue profileReviewEventsDeadLetterQueue() {
        return QueueBuilder.durable("profile.reviews.dlq").build();
    }

    @Bean
    Binding reviewSubmittedBinding(Queue profileReviewEventsQueue, TopicExchange domainEventsExchange) {
        return BindingBuilder.bind(profileReviewEventsQueue).to(domainEventsExchange).with("review.submitted");
    }

    @Bean
    Binding profileReviewDeadLetterBinding(
            Queue profileReviewEventsDeadLetterQueue, TopicExchange profileDeadLetterExchange) {
        return BindingBuilder.bind(profileReviewEventsDeadLetterQueue)
                .to(profileDeadLetterExchange).with("profile.review.failed");
    }

    @Bean
    Jackson2JsonMessageConverter reviewEventMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, Jackson2JsonMessageConverter reviewEventMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(reviewEventMessageConverter);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(reviewEventRetryAdvice());
        return factory;
    }

    @Bean
    RetryOperationsInterceptor reviewEventRetryAdvice() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(500, 2.0, 5000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }
}
