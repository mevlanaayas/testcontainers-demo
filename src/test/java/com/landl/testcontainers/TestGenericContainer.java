package com.landl.testcontainers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class TestGenericContainer {

    private final RestTemplate restTemplate = new RestTemplate();

    @Container
    public GenericContainer notifyApi = new GenericContainer<>("trabz/notify:v1.0.4")
            .withExposedPorts(7070);

    @BeforeEach
    public void setUp() {
        String address = notifyApi.getHost();
        Integer port = notifyApi.getMappedPort(7070);
        System.out.println(String.format("http://%s:%d", address, port));
    }

    @Test
    public void testTest() {
        String address = notifyApi.getHost();
        Integer port = notifyApi.getMappedPort(7070);

        NotifyResponse response = restTemplate.getForObject(String.format("http://%s:%d/status", address, port), NotifyResponse.class);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("I am busy!");
    }

}
