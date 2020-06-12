package com.landl.testcontainers.adapter.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String exchange, String routingKey, RabbitMessage rabbitMessage) {
        log.info("Sending message to exchange: {}, and with body: {}", exchange, rabbitMessage.convertToString());
        rabbitTemplate.convertAndSend(exchange, routingKey, rabbitMessage);
    }
}
