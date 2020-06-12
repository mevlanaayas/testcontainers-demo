package com.landl.testcontainers;


import com.landl.testcontainers.adapter.feign.FeignConfiguration;
import com.landl.testcontainers.adapter.feign.owm.OWMApiAdapter;
import com.landl.testcontainers.adapter.feign.owm.OWMApiCallerService;
import com.landl.testcontainers.adapter.feign.owm.OWMApiConfiguration;
import com.landl.testcontainers.adapter.feign.owm.contract.OwmResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@Testcontainers
@SpringBootTest(classes = {FeignConfiguration.class, OWMApiConfiguration.class})
@ImportAutoConfiguration(classes = {FeignAutoConfiguration.class})
@ContextConfiguration(initializers = {TestMockServer.Initializer.class})
public class TestMockServer {

    private OWMApiCallerService owmApiCallerService;

    @Autowired
    private OWMApiAdapter owmApiAdapter;

    @Autowired
    private OWMApiConfiguration owmApiConfiguration;

    @Container
    public static MockServerContainer mockServer = new MockServerContainer("5.10.0");

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "application.adapter.owm.url=" + mockServer.getEndpoint()
            );
            values.applyTo(applicationContext);
        }
    }

    @BeforeEach
    public void setUp() {
        owmApiCallerService = new OWMApiCallerService(owmApiAdapter, owmApiConfiguration);
        System.out.println(String.format("MockServer container:\nendpoint: %s", mockServer.getEndpoint()));
    }

    @Test
    public void testDummy() {
        // given
        new MockServerClient(mockServer.getHost(), mockServer.getServerPort())
                .when(request()
                        .withMethod("GET")
                        .withPath("/weather?q=ist&appid=test")
                )
                .respond(response()
                        .withStatusCode(200)
                        .withBody("{\"name\":\"ist\",\"main\":{\"temp\":12.1}}"));

        // when
        OwmResponse response = owmApiCallerService.getTemperature("ist");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("ist");
        assertThat(response.getWeather()).isNotNull();
        assertThat(response.getWeather().getTemp()).isBetween(12.0f, 12.2f);
    }
}
