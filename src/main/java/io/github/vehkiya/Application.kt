package io.github.vehkiya

import io.github.vehkiya.config.ApplicationConfiguration
import io.github.vehkiya.config.PersistenceConfiguration
import io.github.vehkiya.service.listener.MessageListener
import io.github.vehkiya.util.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(ApplicationConfiguration::class, PersistenceConfiguration::class)
class Application
@Autowired constructor(
    messageListener: MessageListener
) {

    private val log = logger<Application>()

    init {
        messageListener.listen()
        log.info("Started InfoBot")
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
