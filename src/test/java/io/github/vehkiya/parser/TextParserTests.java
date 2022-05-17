package io.github.vehkiya.parser;

import com.google.common.truth.Truth;
import io.github.vehkiya.config.ApplicationConfiguration;
import io.github.vehkiya.data.model.domain.Item;
import io.github.vehkiya.service.TextParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.truth.TruthJUnit.assume;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = ApplicationConfiguration.class)
class TextParserTests {

    @Autowired
    private TextParser textParser;

    @Test
    void exactMatch() {
        assume().that(textParser).isNotNull();
        final String reference = "Steel Ingot";
        Set<Item> items = textParser.parseItemsFromText(reference);
        items.forEach(item -> {
            Truth.assertThat(item).isNotNull();
            Truth.assertThat(item.name()).containsMatch(reference);
        });
    }

    @Test
    void partialMatch() {
        assume().that(textParser).isNotNull();
        List<String> strings = Arrays.asList("[steel ingot]",
                "[Steel Ingot]",
                "[stEel iNGot]",
                "[steel i]",
                "[Steel Ingot]",
                "[steel ingot ]",
                "[ steel ingot]",
                "[steel    ingot]",
                "<egjer0gj9er> [steel ingot]",
                "[steel bla bla ingot]");
        final String reference = "Steel Ingot";

        strings.stream()
                .map(s -> textParser.parseItemsFromText(s))
                .flatMap(Collection::stream)
                .map(Item::name)
                .forEach(itemName -> Truth.assertThat(itemName).matches(reference));
    }
}