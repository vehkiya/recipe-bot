package io.github.vehkiya;

import io.github.vehkiya.config.SpringConfiguration;
import io.github.vehkiya.exception.InvalidConfigurationException;
import io.github.vehkiya.service.listener.MessageListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

@Log4j2
@SpringBootApplication
@Import(SpringConfiguration.class)
public class Application {

    @Value("${service.integration.key:}")
    private String token;

    @Autowired
    private MessageListener messageListener;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    private void start() {
        log.info("Started InfoBot");
        messageListener.listen();
    }

    @Bean
    public MessageListener messageListener() throws InvalidConfigurationException {
        return new MessageListener(token);
    }

}
