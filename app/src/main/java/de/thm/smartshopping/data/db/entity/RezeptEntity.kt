package de.thm.smartshopping.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rezepte")
data class RezeptEntity(

    @PrimaryKey
    val id: String,

    val name: String,

    val beschreibung: String?,

    val zubereitungszeit: Int,

    val portionen: Int,

    val schwierigkeit: String,

    val kategorie: String,

    val bildPfad: String?
)