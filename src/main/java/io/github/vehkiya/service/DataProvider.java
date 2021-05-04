package io.github.vehkiya.service;

import io.github.vehkiya.data.model.domain.Item;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public interface DataProvider {

    Map<String, Item> itemsCache();

    void refresh() throws IOException;

    default Optional<Item> findByName(String itemName) {
        return Optional.ofNullable(itemName)
                .map(name -> StringUtils.normalizeSpace(name.trim()))
                .map(name -> itemsCache().get(name));
    }
}
