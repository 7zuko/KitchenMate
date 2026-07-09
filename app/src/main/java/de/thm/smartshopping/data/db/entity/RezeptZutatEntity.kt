package de.thm.smartshopping.data.db.entity

import androidx.room.Entity

@Entity(
    tableName = "rezept_zutaten",
    primaryKeys = ["rezeptId", "artikelId"]
)
data class RezeptZutatEntity(

    val rezeptId: String,

    val artikelId: String,

    val menge: Double

)