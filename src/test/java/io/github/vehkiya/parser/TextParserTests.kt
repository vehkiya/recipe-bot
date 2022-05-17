package io.github.vehkiya.parser

import io.github.vehkiya.config.ApplicationConfiguration
import io.github.vehkiya.data.model.domain.Item
import io.github.vehkiya.service.TextParser
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class) @SpringBootTest(classes = [ApplicationConfiguration::class])
class TextParserTests {

    @Autowired
    private lateinit var textParser: TextParser

    private fun validateItemAgainstReference(item: Item, reference: String) {

        Assertions.assertThat(item).isNotNull
        Assertions.assertThat(item.name).contains(reference)
    }

    @Test internal fun `Exact Match`() {
        val reference = "Steel Ingot"
        textParser.parseItemsFromText(reference).forEach {
            validateItemAgainstReference(it, reference)
        }
    }

    @Test internal fun `Partial Match`() {
        val stringList = listOf(
            "[steel ingot]",
            "[Steel Ingot]",
            "[stEel iNGot]",
            "[steel i]",
            "[Steel Ingot]",
            "[steel ingot ]",
            "[ steel ingot]",
            "[steel    ingot]",
            "<egjer0gj9er> [steel ingot]",
            "[steel bla bla ingot]"
        )
        val reference = "Steel Ingot"
        stringList.flatMap { textParser.parseItemsFromText(it) }
            .forEach { validateItemAgainstReference(it, reference) }
    }
}