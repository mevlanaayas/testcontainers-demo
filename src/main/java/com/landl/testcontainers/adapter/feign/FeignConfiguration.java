package com.landl.testcontainers.adapter.feign;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"com.landl.testcontainers.adapter.feign.owm"})
public class FeignConfiguration {
}
