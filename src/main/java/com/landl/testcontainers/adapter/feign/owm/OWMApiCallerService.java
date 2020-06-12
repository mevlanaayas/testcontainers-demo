package com.landl.testcontainers.adapter.feign.owm;

import com.landl.testcontainers.adapter.feign.owm.contract.OwmResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OWMApiCallerService {

    private final OWMApiAdapter owmApiAdapter;
    private final OWMApiConfiguration owmApiConfiguration;

    public OWMApiCallerService(OWMApiAdapter owmApiAdapter,
                               OWMApiConfiguration owmApiConfiguration) {
        this.owmApiAdapter = owmApiAdapter;
        this.owmApiConfiguration = owmApiConfiguration;
    }

    public OwmResponse getTemperature(String cityName) {
        log.info("Sending get request with ciyName: {}", cityName);
        return owmApiAdapter.getTemperature(cityName, owmApiConfiguration.getApiKey());
    }
}
