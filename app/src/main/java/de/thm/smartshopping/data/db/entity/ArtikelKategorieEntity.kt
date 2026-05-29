package de.thm.smartshopping.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artikelkategorien")
data class ArtikelKategorieEntity(
	@PrimaryKey
	val id: String,
	var name: String
)
