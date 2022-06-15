package io.github.vehkiya.data.model.persistence

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "Item") data class Item(
    var name: String, @Id @GeneratedValue var id: Long? = null
)