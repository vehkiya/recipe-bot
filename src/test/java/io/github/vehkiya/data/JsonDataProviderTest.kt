package io.github.vehkiya.data

import io.github.vehkiya.config.ApplicationConfiguration
import io.github.vehkiya.service.provider.JsonDataProvider
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [ApplicationConfiguration::class])
class JsonDataProviderTest {
    @Autowired
    private lateinit var jsonDataProvider: JsonDataProvider

    @Test
    internal fun `Verify Items Load`() {
        val itemName = "Steel Ingot"
        val item = jsonDataProvider.findByName(itemName)
        Assertions.assertThat(item).isNotNull
        Assertions.assertThat(item?.name).isEqualTo(itemName)
    }
}