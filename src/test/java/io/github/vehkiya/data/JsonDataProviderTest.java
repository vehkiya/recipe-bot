package io.github.vehkiya.data;

import com.google.common.truth.Truth8;
import com.google.common.truth.TruthJUnit;
import io.github.vehkiya.config.SpringConfiguration;
import io.github.vehkiya.data.model.Item;
import io.github.vehkiya.service.provider.JsonDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = SpringConfiguration.class)
class JsonDataProviderTest {

    @Autowired
    private JsonDataProvider jsonDataProvider;

    @Test
    void verifyItemsLoad() {
        TruthJUnit.assume().that(jsonDataProvider).isNotNull();
        final String itemName = "Steel Ingot";
        Optional<Item> item = jsonDataProvider.findByName(itemName);
        Truth8.assertThat(item).isPresent();
        Truth8.assertThat(item.map(Item::getName)).hasValue(itemName);
    }
}
