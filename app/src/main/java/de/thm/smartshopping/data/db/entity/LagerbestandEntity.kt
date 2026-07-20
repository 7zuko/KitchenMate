package de.thm.smartshopping.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lagerbestand")
data class LagerbestandEntity(

    @PrimaryKey
    val artikelId: String,

    val menge: Double,

    val mindesthaltbarBis: Long? = null

)