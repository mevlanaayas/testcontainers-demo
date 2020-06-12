package com.landl.testcontainers.domain;

import com.landl.testcontainers.adapter.database.entity.WeatherDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Weather {

    private String cityName;
    private int temperature;

    public Weather(WeatherDto weatherDto) {
        this.cityName = weatherDto.getCityName();
        this.temperature = weatherDto.getTemperature();
    }
}
