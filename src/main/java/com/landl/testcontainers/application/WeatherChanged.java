package com.landl.testcontainers.application;

import com.landl.testcontainers.adapter.rabbit.RabbitMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WeatherChanged implements RabbitMessage {

    private final String cityName;

    @Override
    public String convertToString() {
        return cityName;
    }
}
