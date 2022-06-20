package io.github.vehkiya.service

import io.github.vehkiya.data.model.domain.Item
import org.apache.commons.lang3.StringUtils

interface DataProvider {

    fun itemsCache(): Map<String, Item>

    fun refresh()

    fun findByName(itemName: String): Item? {
        val normalizedName = StringUtils.normalizeSpace(itemName.trim())
        return itemsCache()[normalizedName]
    }
}