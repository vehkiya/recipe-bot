package io.github.vehkiya.data.model.domain

data class Item(val name: String) {
    var recipe: Recipe? = null
    val alternativeRecipes: Set<Recipe> = emptySet()
    fun name(): String {
        return name
    }
}
