package io.github.vehkiya.data.repository

import io.github.vehkiya.data.model.persistence.Item
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository : JpaRepository<Item, Long> {
    fun findByName(name: String): Item
}