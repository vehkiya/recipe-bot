package io.github.vehkiya.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.vehkiya.exception.InvalidConfigurationException
import io.github.vehkiya.service.DataProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@ComponentScan(
    "io.github.vehkiya.controller",
    "io.github.vehkiya.config",
    "io.github.vehkiya.service.listener",
    "io.github.vehkiya.service.parser"
)
@SpringBootConfiguration
@EnableConfigurationProperties class ApplicationConfiguration {

    @Autowired
    lateinit var serviceProviderProperties: ServiceProviderProperties

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Bean fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply { registerKotlinModule() }
    }

    @Bean fun dataProvider(): DataProvider {
        val beanFactory = applicationContext.autowireCapableBeanFactory
        val classLoader = applicationContext.classLoader
        classLoader ?: throw InvalidConfigurationException("Null classLoader")
        try {
            val loadClass = classLoader.loadClass(serviceProviderProperties.className)
            return beanFactory.autowire(loadClass, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true) as DataProvider
        } catch (e: ClassNotFoundException) {
            throw InvalidConfigurationException("Cannot Find class ${serviceProviderProperties.className}");
        } catch (e: ClassCastException) {
            throw InvalidConfigurationException("Class ${serviceProviderProperties.className} is not a DataProvider type");
        }
    }
}