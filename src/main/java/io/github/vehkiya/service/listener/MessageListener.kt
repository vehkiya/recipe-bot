package io.github.vehkiya.service.listener

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import io.github.vehkiya.data.model.domain.Item
import io.github.vehkiya.service.TextParser
import io.github.vehkiya.util.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class MessageListener
@Autowired constructor(
    @Value("\${service.integration.key:}") token: String,
    val textParser: TextParser
) {

    private val log = logger<MessageListener>()
    private val defaultTimeout = 500L

    private val gateway: GatewayDiscordClient

    init {
        require(token.isNotEmpty()) {
            "Cannot start without credentials. Please set [service.integration.key] property"
        }
        DiscordClient.create(token).apply {
            gateway = login().block() as GatewayDiscordClient
        }
    }

    fun listen() {
        val messageFlux = gateway.on(MessageCreateEvent::class.java).onErrorContinue(
            java.lang.RuntimeException::class.java
        ) { throwable, _ -> log.error(throwable) }

        messageFlux.subscribe(this::processMessage, log::error)
        gateway.onDisconnect().block()
    }

    private fun processMessage(event: MessageCreateEvent) {
        if (shouldReply(event.message)) {
            val items = textParser.parseItemsFromText(event.message.content)
            event.message.channel.blockOptional(Duration.ofMillis(defaultTimeout))
                .ifPresent { channel -> reply(channel, items) }
        }
    }

    private fun reply(messageChannel: MessageChannel, items: Set<Item>) {
        when (items) {
            emptySet<Item>() -> messageChannel.createMessage("I could not find any matching items for your query :(")
                .block()
            else -> messageChannel.createMessage(
                "I found following items you mentioned:${System.lineSeparator()}" + buildResponseMessage(items)
            ).block()
        }
    }

    private fun shouldReply(message: Message): Boolean {
        return !message.author.map { it.isBot }.orElse(true) && textParser.messageMatchesPattern(message.content)
    }

    fun buildResponseMessage(items: Set<Item>): String {
        val stringBuilder = StringBuilder()
        items.sortedBy { item -> item.name }.take(10).forEach {
            stringBuilder.append(it.name)
            stringBuilder.append(System.lineSeparator())
        }
        return stringBuilder.toString()
    }

}