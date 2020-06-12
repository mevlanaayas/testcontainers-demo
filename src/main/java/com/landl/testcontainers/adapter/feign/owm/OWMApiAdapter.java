package com.landl.testcontainers.adapter.feign.owm;

import com.landl.testcontainers.adapter.feign.owm.contract.OwmResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "owm", url = "${application.adapter.owm.url}")
public interface OWMApiAdapter {

    @GetMapping(value = "/weather?q={cityName}&appid={apiKey}")
    OwmResponse getTemperature(@PathVariable("cityName") String cityName, @PathVariable("apiKey") String apiKey);
}
