package io.github.vehkiya.service

import io.github.vehkiya.data.model.domain.Item

interface TextParser {

    fun messageMatchesPattern(text: String): Boolean

    fun parseItemsFromText(text: String): Set<Item>
}
