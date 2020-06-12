package com.landl.testcontainers.adapter.rabbit;

import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

import java.util.Map;
import java.util.UUID;


public class RabbitMqMessagePostProcessor implements MessagePostProcessor {

    public static final String AGENT_NAME = "AgentName";
    public static final String CORRELATION_ID = "CorrelationId";

    @Override
    public Message postProcessMessage(Message message) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();

        String correlationId = headers.getOrDefault(CORRELATION_ID, UUID.randomUUID()).toString();

        MDC.put(CORRELATION_ID, correlationId);
        MDC.put(AGENT_NAME, "Agent");

        return message;
    }
}
