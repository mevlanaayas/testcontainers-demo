package com.landl.testcontainers.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landl.testcontainers.adapter.database.WeatherDatabaseAdapter;
import com.landl.testcontainers.domain.Weather;
import com.landl.testcontainers.adapter.feign.owm.OWMApiCallerService;
import com.landl.testcontainers.adapter.feign.owm.contract.OwmResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class WeatherService {

    private final WeatherDatabaseAdapter weatherDatabaseAdapter;
    private final ObjectMapper objectMapper;
    private final OWMApiCallerService owmApiCallerService;

    public WeatherService(WeatherDatabaseAdapter weatherDatabaseAdapter,
                          ObjectMapper objectMapper,
                          OWMApiCallerService owmApiCallerService) {
        this.weatherDatabaseAdapter = weatherDatabaseAdapter;
        this.objectMapper = objectMapper;
        this.owmApiCallerService = owmApiCallerService;
    }

    public void saveWeatherRecord(byte[] body) throws IOException {
        WeatherRequested weatherRequested = objectMapper.readValue(body, WeatherRequested.class);
        OwmResponse owmResponse = owmApiCallerService.getTemperature(weatherRequested.getCityName());
        Weather weather = new Weather(owmResponse.getName(), (int) owmResponse.getWeather().getTemp());
        weatherDatabaseAdapter.save(weather);
    }
}
