package io.github.vehkiya.service.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vehkiya.config.ServiceProviderProperties;
import io.github.vehkiya.data.model.domain.Item;
import io.github.vehkiya.data.model.json.JsonItem;
import io.github.vehkiya.data.model.json.JsonSource;
import io.github.vehkiya.service.DataProvider;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class JsonDataProvider implements DataProvider {

    @Autowired
    private ServiceProviderProperties serviceProviderProperties;

    @Getter
    @Accessors(fluent = true)
    private Map<String, Item> itemsCache;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
        itemsCache = new ConcurrentHashMap<>();
        refresh();
    }

    @Override
    public void refresh() throws IOException {
        itemsCache.clear();
        var jsonSource = readJsonFile();
        jsonSource.getItems().values()
                .stream()
                .filter(Objects::nonNull)
                .filter(jsonItem -> Objects.nonNull(jsonItem.name()))
                .map(itemConverter())
                .forEach(item -> itemsCache.put(item.name(), item));
    }

    private Function<JsonItem, Item> itemConverter() {
        return jsonItem -> new Item().name(jsonItem.name());
    }

    private JsonSource readJsonFile() throws IOException {
        var path = Paths.get(serviceProviderProperties.getSource());
        if (!path.toFile().exists()) {
            throw new IllegalArgumentException("Source file " + path + " not found ");
        }
        var content = new String(Files.readAllBytes(path));
        return objectMapper.readValue(content, JsonSource.class);
    }


}
