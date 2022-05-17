package io.github.vehkiya.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "service.parser")
public class ServiceParserProperties {
    private Float threshold;
    private String pattern;
    private String indexPath;

    public Float getThreshold() {
        return threshold;
    }

    public ServiceParserProperties setThreshold(Float threshold) {
        this.threshold = threshold;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public ServiceParserProperties setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public String getIndexPath() {
        return indexPath;
    }

    public ServiceParserProperties setIndexPath(String indexPath) {
        this.indexPath = indexPath;
        return this;
    }

    @PostConstruct
    private void init() {
        if (threshold == null) {
            threshold = 1.0f;
        }
    }
}
