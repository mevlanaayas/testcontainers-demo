package com.landl.testcontainers.controller;

import com.landl.testcontainers.application.GetWeatherQuery;
import com.landl.testcontainers.application.PublishWeatherEventQuery;
import com.landl.testcontainers.application.WeatherChanged;
import com.landl.testcontainers.adapter.database.WeatherDatabaseAdapter;
import com.landl.testcontainers.domain.Weather;
import com.landl.testcontainers.adapter.rabbit.RabbitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class WeatherController {
    private static final String PROD_EXCHANGE = "prodExchange";

    private final WeatherDatabaseAdapter weatherDatabaseAdapter;
    private final RabbitService rabbitService;

    public WeatherController(WeatherDatabaseAdapter weatherDatabaseAdapter,
                             RabbitService rabbitService) {
        this.weatherDatabaseAdapter = weatherDatabaseAdapter;
        this.rabbitService = rabbitService;
    }

    @GetMapping("/weather")
    public ResponseEntity<List<Weather>> get(GetWeatherQuery getWeatherQuery) {
        return ResponseEntity.ok(weatherDatabaseAdapter.getWeather(getWeatherQuery.getCityName()));
    }

    @GetMapping("/publish")
    public ResponseEntity<String> get(PublishWeatherEventQuery publishWeatherEventQuery) {
        rabbitService.publish(PROD_EXCHANGE, PROD_EXCHANGE, new WeatherChanged(publishWeatherEventQuery.getCityName()));
        return ResponseEntity.ok("published");
    }
}
