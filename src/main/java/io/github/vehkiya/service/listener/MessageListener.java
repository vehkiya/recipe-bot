package io.github.vehkiya.service.listener;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import io.github.vehkiya.data.model.domain.Item;
import io.github.vehkiya.exception.InvalidConfigurationException;
import io.github.vehkiya.service.TextParser;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Comparator;
import java.util.Set;

@Log4j2
public class MessageListener {

    private final String token;
    private static final int DEFAULT_TIMEOUT = 500;

    @Autowired
    private TextParser textParser;

    private GatewayDiscordClient gateway;

    public MessageListener(String token) throws InvalidConfigurationException {
        if (StringUtils.isEmpty(token)) {
            throw new InvalidConfigurationException("Cannot start without credentials. Please set service.integration.key property");
        }
        this.token = token;
    }

    @PostConstruct
    private void init() {
        var client = DiscordClient.create(token);
        gateway = client.login().block();
    }

    public void listen() {
        var messageFlux = gateway.on(MessageCreateEvent.class)
                .onErrorContinue(RuntimeException.class, (throwable, o) -> log.error(throwable));

        messageFlux.subscribe(this::processMessage, log::error);
        gateway.onDisconnect().block();
    }

    private void processMessage(MessageCreateEvent event) {
        final var message = event.getMessage();
        if (shouldReply(message)) {
            var items = textParser.parseItemsFromText(message.getContent());
            message.getChannel()
                    .blockOptional(Duration.ofMillis(DEFAULT_TIMEOUT))
                    .ifPresent(messageChannel -> reply(messageChannel, items));
        }
    }

    private void reply(MessageChannel messageChannel, Set<Item> items) {
        if (items.isEmpty()) {
            messageChannel.createMessage("I could not find any matching items for your query :(").block();
        } else {
            var response = "I found following items you mentioned:" +
                    System.lineSeparator() +
                    buildResponseMessage(items);
            messageChannel.createMessage(response).block();
        }
    }

    private boolean shouldReply(Message message) {
        return !message.getAuthor().map(User::isBot).orElse(true)
                && textParser.messageMatchesPattern(message.getContent());
    }


    public String buildResponseMessage(Set<Item> items) {
        var stringBuilder = new StringBuilder();
        items.stream()
                .sorted(Comparator.comparing(Item::name))
                .limit(10)
                .forEachOrdered(i -> {
                    stringBuilder.append(i.name());
                    stringBuilder.append(System.lineSeparator());
                });
        return stringBuilder.toString();
    }

}
