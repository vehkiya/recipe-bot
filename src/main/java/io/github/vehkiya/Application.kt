package io.github.vehkiya

import io.github.vehkiya.config.ApplicationConfiguration
import io.github.vehkiya.service.listener.MessageListener
import io.github.vehkiya.util.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import javax.annotation.PostConstruct

@SpringBootApplication @Import(ApplicationConfiguration::class) class Application {

    private val log = logger<Application>()

    @Value("\${service.integration.key:}")
    private lateinit var token: String

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @PostConstruct fun startListener() {
        val messageListener = MessageListener(token)
        applicationContext.autowireCapableBeanFactory.autowireBean(messageListener)
        messageListener.listen()
        log.info("Started InfoBot")
    }
}


fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
