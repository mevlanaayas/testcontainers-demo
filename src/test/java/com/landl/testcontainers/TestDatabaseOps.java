package com.landl.testcontainers;

import com.landl.testcontainers.adapter.database.WeatherDatabaseAdapter;
import com.landl.testcontainers.adapter.database.repository.WeatherRepository;
import com.landl.testcontainers.domain.Weather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = TestDatabaseOps.Initializer.class)
public class TestDatabaseOps {

    private WeatherDatabaseAdapter weatherDatabaseAdapter;

    @Autowired
    private WeatherRepository weatherRepository;

    @Container
    public static MSSQLServerContainer mssqlServerContainer = new MSSQLServerContainer();

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.datasource.url=" + mssqlServerContainer.getJdbcUrl(),
                    "spring.datasource.username=" + mssqlServerContainer.getUsername(),
                    "spring.datasource.password=" + mssqlServerContainer.getPassword()
            );
            values.applyTo(applicationContext);
        }
    }

    @BeforeEach
    public void setUp() {

        weatherDatabaseAdapter = new WeatherDatabaseAdapter(weatherRepository);
        System.out.println(String.format("MSSQL container:\njdbcUrl: %s\nusername: %s\npass: %s\nport: %s",
                mssqlServerContainer.getJdbcUrl(),
                mssqlServerContainer.getUsername(),
                mssqlServerContainer.getPassword(),
                mssqlServerContainer.getMappedPort(1433)));
    }

    @Test
    public void testWriteWeather() {
        // given
        weatherDatabaseAdapter.save(new Weather("ist", 1));
        weatherDatabaseAdapter.save(new Weather("ist", 10));

        // when
        List<Weather> weather = weatherDatabaseAdapter.getWeather("ist");

        // then
        assertThat(weather).hasSize(2);
    }
}
