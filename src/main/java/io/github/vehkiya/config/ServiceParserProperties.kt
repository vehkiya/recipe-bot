package io.github.vehkiya.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "service.parser")
class ServiceParserProperties {

    var threshold: Float = 1.0f
    lateinit var pattern: String
    lateinit var indexPath: String
}