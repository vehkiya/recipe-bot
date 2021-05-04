package io.github.vehkiya.data.model.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true, fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Recipe {

    private Map<Item, Integer> ingredients;

}
