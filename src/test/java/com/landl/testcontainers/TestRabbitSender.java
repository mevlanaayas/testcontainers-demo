package com.landl.testcontainers;

import com.landl.testcontainers.adapter.rabbit.RabbitConfiguration;
import com.landl.testcontainers.adapter.rabbit.RabbitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = {RabbitConfiguration.class})
@ImportAutoConfiguration(classes = {RabbitAutoConfiguration.class, JacksonAutoConfiguration.class})
@ContextConfiguration(initializers = TestRabbitSender.Initializer.class)
public class TestRabbitSender {

    @Container
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer()
            .withExchange("testExchange", "fanout")
            .withQueue("prodQueue")
            .withBinding("testExchange", "prodQueue");

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.rabbitmq.host=" + rabbitMQContainer.getContainerIpAddress(),
                    "spring.rabbitmq.port=" + rabbitMQContainer.getAmqpPort(),
                    "spring.rabbitmq.username=" + rabbitMQContainer.getAdminUsername(),
                    "spring.rabbitmq.password=" + rabbitMQContainer.getAdminPassword()
            );
            values.applyTo(applicationContext);
        }
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private RabbitService rabbitService;

    @BeforeEach
    public void setUp() {
        rabbitService = new RabbitService(rabbitTemplate);
        System.out.println(String.format("Rabbit container:\nusername: %s\npass: %s\nport 5672: %s\nport 15672: %s",
                rabbitMQContainer.getAdminUsername(),
                rabbitMQContainer.getAdminPassword(),
                rabbitMQContainer.getAmqpPort(),
                rabbitMQContainer.getHttpPort()));
    }

    @Test
    public void testSenderService() throws IOException {
        // given
        RabbitEvent test_message = new RabbitEvent("test message");

        // when
        rabbitService.publish("testExchange", "testRoutingKey", test_message);

        // then
        Message message = rabbitTemplate.receive("prodQueue", 2000);

        assertThat(message).isNotNull();
        RabbitEvent actual = new ObjectMapper().readValue(message.getBody(), RabbitEvent.class);
        assertThat(actual).isNotNull();
        assertThat(actual.getMessage()).isEqualTo("test message");
    }
}
