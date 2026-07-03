package de.thm.smartshopping.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RezeptZutatEntity(
    @PrimaryKey
    val id: String,

    val rezeptId: String,
    val artikelId: String,
    val menge: Double
)