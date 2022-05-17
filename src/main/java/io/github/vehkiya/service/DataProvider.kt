package io.github.vehkiya.service

import io.github.vehkiya.data.model.domain.Item
import org.apache.commons.lang3.StringUtils
import java.util.*

interface DataProvider {

    fun itemsCache(): Map<String, Item>

    fun refresh()

    fun findByName(itemName: String): Optional<Item> {
        return Optional.ofNullable(itemName)
            .map { StringUtils.normalizeSpace(it.trim()) }
            .map { itemsCache()[it] }
    }
}