package com.landl.testcontainers.adapter.database;

import com.landl.testcontainers.domain.Weather;
import com.landl.testcontainers.adapter.database.entity.WeatherDto;
import com.landl.testcontainers.adapter.database.repository.WeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeatherDatabaseAdapter {

    private final WeatherRepository weatherRepository;

    public WeatherDatabaseAdapter(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public void save(Weather weather) {
        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setCityName(weather.getCityName());
        weatherDto.setTemperature(weather.getTemperature());
        weatherRepository.save(weatherDto);
    }

    public List<Weather> getWeather(String cityName) {
        return weatherRepository
                .findAllByCityName(cityName)
                .stream()
                .map(Weather::new)
                .collect(Collectors.toList());
    }
}
