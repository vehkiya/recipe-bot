package io.github.vehkiya.data.model.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true, fluent = true)
public class Item {

    private String name;

    private Recipe recipe;

    private Set<Recipe> alternativeRecipes;

}
