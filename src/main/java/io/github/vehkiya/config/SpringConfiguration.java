package io.github.vehkiya.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"io.github.vehkiya.controller",
        "io.github.vehkiya.service",
        "io.github.vehkiya.config"})
@SpringBootConfiguration
@EnableConfigurationProperties
public class SpringConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

