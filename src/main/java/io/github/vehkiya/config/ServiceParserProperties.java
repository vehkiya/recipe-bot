package io.github.vehkiya.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
@ConfigurationProperties(prefix = "service.parser")
public class ServiceParserProperties {

    private Float threshold;

    private String pattern;

    private String indexPath;

    @PostConstruct
    public void init() {
        if (threshold == null) {
            threshold = 1.0f;
        }
    }
}
