package com.landl.testcontainers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(properties = "")
public class TestDockerCompose {

    @Container
    public static DockerComposeContainer environment =
            new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService("redis_1", 6379)
                    .withExposedService("elasticsearch_1", 9200);
                    // .waitingFor("redis_1", Wait.forLogMessage("started", 1));


    @BeforeEach
    public void setUp() {
        System.out.println(String.format("Docker Compose container:\nredis host:port: %s:%d\nes host:port: %s:%d",
                environment.getServiceHost("redis_1", 6379),
                environment.getServicePort("redis_1", 6379),
                environment.getServiceHost("elasticsearch_1", 9200),
                environment.getServicePort("elasticsearch_1", 9200)));
    }

    @Test
    public void testDummy() {
        assertThat("haveFun").isEqualTo("haveFun");
    }
}
