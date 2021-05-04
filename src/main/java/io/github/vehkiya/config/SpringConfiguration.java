package io.github.vehkiya.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vehkiya.exception.InvalidConfigurationException;
import io.github.vehkiya.service.DataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Objects;

@ComponentScan({
        "io.github.vehkiya.controller",
        "io.github.vehkiya.config",
        "io.github.vehkiya.service.listener",
        "io.github.vehkiya.service.parser"
})
@SpringBootConfiguration
@EnableConfigurationProperties
public class SpringConfiguration {

    @Autowired
    private ServiceProviderProperties serviceProviderProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public DataProvider dataProvider() throws InvalidConfigurationException {
        var beanFactory = applicationContext.getAutowireCapableBeanFactory();
        var classLoader = applicationContext.getClassLoader();
        if (Objects.isNull(classLoader)) {
            throw new InvalidConfigurationException("Null classLoader");
        }
        try {
            Class<? extends DataProvider> loadClass = (Class<? extends DataProvider>) classLoader.loadClass(serviceProviderProperties.getClassName());
            return (DataProvider) beanFactory.autowire(loadClass, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        } catch (ClassNotFoundException e) {
            throw new InvalidConfigurationException("Cannot Find class " + serviceProviderProperties.getClassName());
        } catch (ClassCastException e) {
            throw new InvalidConfigurationException("Class " + serviceProviderProperties.getClassName() + " is not a DataProvider type");
        }
    }
}

