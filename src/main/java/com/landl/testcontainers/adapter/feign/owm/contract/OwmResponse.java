package com.landl.testcontainers.adapter.feign.owm.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.landl.testcontainers.domain.Main;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OwmResponse {

    private String name;
    @JsonProperty("main")
    private Main weather;
}
