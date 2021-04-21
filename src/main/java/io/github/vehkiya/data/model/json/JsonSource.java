package io.github.vehkiya.data.model.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonSource {

    private Map<String, JsonItem> items;
}
