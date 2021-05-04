package io.github.vehkiya.data.model.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonItem {

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("name")
    private String name;
}
