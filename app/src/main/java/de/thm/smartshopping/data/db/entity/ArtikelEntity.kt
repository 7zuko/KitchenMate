package de.thm.smartshopping.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "artikel",
	primaryKeys = ["id"],
	foreignKeys = [
		ForeignKey(
			entity = ArtikelKategorieEntity::class,
			parentColumns = ["id"],
			childColumns = ["kategorieId"],
			onDelete = ForeignKey.RESTRICT
		)
	]
	)
data class ArtikelEntity(
	val id: String,
	var name: String,
	val einheit: String? = null,
	val kategorieId: String? = null
)
