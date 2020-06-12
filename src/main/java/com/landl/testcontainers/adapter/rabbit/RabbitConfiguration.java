package com.landl.testcontainers.adapter.rabbit;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(value = "spring.rabbitmq")
public class RabbitConfiguration {

    private static final Integer CONCURRENT_CONSUMER = 2;
    private static final Integer MAX_CONCURRENT_CONSUMER = 10;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                                               ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(CONCURRENT_CONSUMER);
        factory.setMaxConcurrentConsumers(MAX_CONCURRENT_CONSUMER);
        factory.setAfterReceivePostProcessors(new RabbitMqMessagePostProcessor());

        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ObjectMapper objectMapper, ConnectionFactory defaultConnectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(defaultConnectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
        return rabbitTemplate;
    }
}
