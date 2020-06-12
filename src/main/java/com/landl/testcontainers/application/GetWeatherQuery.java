package com.landl.testcontainers.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetWeatherQuery {

    private final String cityName;
}
