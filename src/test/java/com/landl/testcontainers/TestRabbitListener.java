package com.landl.testcontainers;

import com.landl.testcontainers.adapter.rabbit.RabbitConfiguration;
import com.landl.testcontainers.adapter.rabbit.listener.MessageListener;
import com.landl.testcontainers.application.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest(classes = {MessageListener.class, RabbitConfiguration.class})
@ContextConfiguration(initializers = TestRabbitListener.Initializer.class)
@ImportAutoConfiguration(classes = {RabbitAutoConfiguration.class, JacksonAutoConfiguration.class})
public class TestRabbitListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private WeatherService weatherService;

    @Captor
    private ArgumentCaptor<byte[]> rabbitEventArgumentCaptor;

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

    @BeforeEach
    public void setUp() {
        System.out.println(String.format("Rabbit container:\nusername: %s\npass: %s\nport 5672: %s\nport 15672: %s",
                rabbitMQContainer.getAdminUsername(),
                rabbitMQContainer.getAdminPassword(),
                rabbitMQContainer.getAmqpPort(),
                rabbitMQContainer.getHttpPort()));
    }

    @Test
    public void testListener() throws IOException, InterruptedException {
        // given
        RabbitEvent test_message = new RabbitEvent("test message");

        // when
        rabbitTemplate.convertAndSend("testExchange", "testExchange", test_message);
        Thread.sleep(2000);

        // then
        verify(weatherService).saveWeatherRecord(rabbitEventArgumentCaptor.capture());

        byte[] body = rabbitEventArgumentCaptor.getValue();
        assertThat(body).isNotNull();
        RabbitEvent actual = new ObjectMapper().readValue(body, RabbitEvent.class);
        assertThat(actual).isNotNull();
        assertThat(actual.getMessage()).isEqualTo("test message");
    }
}
