package io.github.vehkiya.service.provider

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.vehkiya.config.ServiceProviderProperties
import io.github.vehkiya.data.model.domain.Item
import io.github.vehkiya.data.model.json.JsonSource
import io.github.vehkiya.service.DataProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.notExists

@Service
class JsonDataProvider
@Autowired constructor(
    val objectMapper: ObjectMapper,
    val properties: ServiceProviderProperties
) : DataProvider {

    private val itemsCache: MutableMap<String, Item> = mutableMapOf()

    init {
        refresh()
    }

    override fun itemsCache(): Map<String, Item> {
        return itemsCache
    }

    final override fun refresh() {
        itemsCache.clear()
        val jsonSource = readJsonFile()
        jsonSource.items
            .values
            .map { convert { Item(it.name) } }
            .forEach { itemsCache[it.name] = it }
    }

    private inline fun <T> convert(mapper: () -> T): T = mapper.invoke()

    private fun readJsonFile(): JsonSource {
        val path = Path(properties.source)
        if (path.notExists()) {
            throw IllegalArgumentException("Source file $path not found")
        }
        val content = Files.readAllBytes(path)
        return objectMapper.readValue(content, JsonSource::class.java)
    }
}