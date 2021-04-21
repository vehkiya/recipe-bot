package io.github.vehkiya.service;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import io.github.vehkiya.data.model.Item;
import io.github.vehkiya.exception.InvalidConfigurationException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Consumer;

@Log4j2
public class MessageListener {

    private final String token;

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
    public void init() {
        DiscordClient client = DiscordClient.create(token);
        gateway = client.login().block();
    }

    public void listen() {
        Flux<MessageCreateEvent> messageFlux = gateway.on(MessageCreateEvent.class)
                .onErrorContinue(RuntimeException.class, (throwable, o) -> log.error(throwable));

        messageFlux.subscribe(processMessage(), log::error);
        gateway.onDisconnect().block();
    }

    private Consumer<MessageCreateEvent> processMessage() {
        return event -> {
            final Message message = event.getMessage();
            if (shouldReply(message)) {
                final MessageChannel channel = message.getChannel().block();
                Set<Item> items = textParser.parseItemsFromText(message.getContent());
                if (items.isEmpty()) {
                    channel.createMessage("I could not find any matching items for your query :(").block();
                } else {
                    String response = buildResponseMessage(items);
                    String stringBuilder = "I found following items you mentioned:" +
                            System.lineSeparator() +
                            response;
                    channel.createMessage(stringBuilder).block();
                }
            }
        };
    }

    private boolean shouldReply(Message message) {
        return !message.getAuthor().map(User::isBot).orElse(true)
                && textParser.messageMatchesPattern(message.getContent());
    }


    public String buildResponseMessage(Set<Item> items) {
        StringBuilder stringBuilder = new StringBuilder();
        items.stream()
                .sorted(Comparator.comparing(Item::getName))
                .limit(10)
                .forEachOrdered(i -> {
                    stringBuilder.append(i.getName());
                    stringBuilder.append(System.lineSeparator());
                });
        return stringBuilder.toString();
    }

}
