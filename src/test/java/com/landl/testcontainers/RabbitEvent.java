package com.landl.testcontainers;

import com.landl.testcontainers.adapter.rabbit.RabbitMessage;

public class RabbitEvent implements RabbitMessage {

    private String message;

    public RabbitEvent() {
    }

    public RabbitEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String convertToString() {
        return message;
    }
}
