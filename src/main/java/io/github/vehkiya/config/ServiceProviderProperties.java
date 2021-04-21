package io.github.vehkiya.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "service.provider")
public class ServiceProviderProperties {

    private String className;

    private String source;
}
