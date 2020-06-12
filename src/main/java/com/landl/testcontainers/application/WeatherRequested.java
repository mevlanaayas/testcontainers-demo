package com.landl.testcontainers.application;

import com.landl.testcontainers.adapter.rabbit.RabbitMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WeatherRequested implements RabbitMessage {

    private String cityName;

    @Override
    public String convertToString() {
        return null;
    }
}
