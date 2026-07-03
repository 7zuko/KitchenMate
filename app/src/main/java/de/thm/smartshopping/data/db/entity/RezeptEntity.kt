package de.thm.smartshopping.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RezeptEntity(
    @PrimaryKey
    val id: String,

    val name: String,
    val beschreibung: String?,
    val zubereitungszeit: Int,
    val bildPfad: String?
)