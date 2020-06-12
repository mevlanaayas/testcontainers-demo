package com.landl.testcontainers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {TestEndToEnd.Initializer.class})
public class TestEndToEnd {
    @Container
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer()
            .withExchange("testExchange", "fanout")
            .withQueue("prodQueue")
            .withBinding("testExchange", "prodQueue");

    @Container
    public static MSSQLServerContainer mssqlServerContainer = new MSSQLServerContainer();

    @Container
    public static MockServerContainer mockServer = new MockServerContainer();


    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.rabbitmq.host=" + rabbitMQContainer.getContainerIpAddress(),
                    "spring.rabbitmq.port=" + rabbitMQContainer.getAmqpPort(),
                    "spring.rabbitmq.username=" + rabbitMQContainer.getAdminUsername(),
                    "spring.rabbitmq.password=" + rabbitMQContainer.getAdminPassword(),

                    "spring.datasource.url=" + mssqlServerContainer.getJdbcUrl(),
                    "spring.datasource.username=" + mssqlServerContainer.getUsername(),
                    "spring.datasource.password=" + mssqlServerContainer.getPassword(),

                    "application.adapter.owm.url=" + mockServer.getEndpoint()
            );
            values.applyTo(applicationContext);
        }
    }

    @BeforeEach
    public void setUp() {
        System.out.println(String.format("Rabbit container:\nusername: %s\npass: %s\nport 5672: %s\nport 15672: %s\n",
                rabbitMQContainer.getAdminUsername(),
                rabbitMQContainer.getAdminPassword(),
                rabbitMQContainer.getAmqpPort(),
                rabbitMQContainer.getHttpPort()));

        System.out.println(String.format("MSSQL container:\njdbcUrl: %s\nusername: %s\npass: %s\nport: %s\n",
                mssqlServerContainer.getJdbcUrl(),
                mssqlServerContainer.getUsername(),
                mssqlServerContainer.getPassword(),
                mssqlServerContainer.getMappedPort(1433)));

        System.out.println(String.format("MockServer container:\nendpoint: %s\n", mockServer.getEndpoint()));
    }

    @Test
    public void testDummy() {
        assertThat("hello world").isEqualTo("hello world");
    }
}
