package io.github.vehkiya.service.provider

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.vehkiya.config.ServiceProviderProperties
import io.github.vehkiya.data.model.domain.Item
import io.github.vehkiya.data.model.json.JsonSource
import io.github.vehkiya.service.DataProvider
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Files
import javax.annotation.PostConstruct
import kotlin.io.path.Path
import kotlin.io.path.notExists

class JsonDataProvider : DataProvider {

    private val itemsCache: MutableMap<String, Item> = mutableMapOf()

    lateinit var objectMapper: ObjectMapper
    lateinit var properties: ServiceProviderProperties

    //todo: make this smarter
    @Autowired
    fun initBeans(
        objectMapper: ObjectMapper,
        properties: ServiceProviderProperties
    ) {
        this.objectMapper = objectMapper
        this.properties = properties
    }

    @PostConstruct
    fun postConstruct() {
        refresh()
    }

    override fun itemsCache(): Map<String, Item> {
        return itemsCache
    }

    override fun refresh() {
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