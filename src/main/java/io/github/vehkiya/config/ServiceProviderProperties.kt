package io.github.vehkiya.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "service.provider")
class ServiceProviderProperties {

    lateinit var className: String
    lateinit var source: String
}