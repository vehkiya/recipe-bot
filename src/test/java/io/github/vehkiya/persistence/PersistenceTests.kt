package io.github.vehkiya.persistence

import io.github.vehkiya.config.PersistenceConfiguration
import io.github.vehkiya.data.model.persistence.Item
import io.github.vehkiya.data.repository.ItemRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [PersistenceConfiguration::class])
class PersistenceTests
@Autowired constructor(val itemRepository: ItemRepository) {

    @Test
    internal fun `Database write`() {
        val item = Item("steel ingot")
        itemRepository.save(item)
        val items = itemRepository.findAll()
        Assertions.assertThat(items).isNotEmpty
        Assertions.assertThat(items).contains(item)
    }
}