package com.landl.testcontainers.adapter.database.repository;

import com.landl.testcontainers.adapter.database.entity.WeatherDto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WeatherRepository extends CrudRepository<WeatherDto, Integer> {

    List<WeatherDto> findAllByCityName(String cityName);
}
