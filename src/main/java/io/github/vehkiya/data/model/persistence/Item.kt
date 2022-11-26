package io.github.vehkiya.data.model.persistence

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "Item")
data class Item(
    var name: String, @Id @GeneratedValue var id: Long? = null
)