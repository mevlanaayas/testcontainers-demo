package com.landl.testcontainers.adapter.rabbit.listener;

import com.landl.testcontainers.application.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
public class MessageListener {

    private static final String PROD_QUEUE = "prodQueue";

    private final WeatherService weatherService;

    public MessageListener(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @RabbitListener(queues = PROD_QUEUE)
    public void testListener(Message message) throws IOException {
        weatherService.saveWeatherRecord(message.getBody());
    }
}
