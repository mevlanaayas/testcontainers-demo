package com.landl.testcontainers.adapter.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "weather", schema = "dbo")
public class WeatherDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cityName")
    private String cityName;

    @Column(name = "temperature")
    private Integer temperature;
}
