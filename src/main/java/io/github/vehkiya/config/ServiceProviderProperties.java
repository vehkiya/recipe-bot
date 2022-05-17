package io.github.vehkiya.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "service.provider")
public class ServiceProviderProperties {

    private String className;

    private String source;

    public String getClassName() {
        return className;
    }

    public String getSource() {
        return source;
    }
}
