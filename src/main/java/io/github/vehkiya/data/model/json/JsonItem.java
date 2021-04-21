package io.github.vehkiya.data.model.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.vehkiya.data.model.Item;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonItem implements Item {

    private String slug;

    private String name;
}
