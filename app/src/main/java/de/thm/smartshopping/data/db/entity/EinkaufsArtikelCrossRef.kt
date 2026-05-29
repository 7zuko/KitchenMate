package de.thm.smartshopping.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
	tableName = "einkaufslisten_artikel",
	primaryKeys = ["einkaufslisteId", "artikelId"],
	foreignKeys = [
		ForeignKey(
			entity = EinkaufslisteEntity::class,
			parentColumns = ["id"],
			childColumns = ["einkaufslisteId"],
			onDelete = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = ArtikelEntity::class,
			parentColumns = ["id"],
			childColumns = ["artikelId"],
			onDelete = ForeignKey.RESTRICT
		)
	],
	indices = [Index("einkaufslisteId"), Index("artikelId")]
)
data class EinkaufsArtikelCrossRef(
	val einkaufslisteId: String,
	val artikelId: String,
	val menge: Double,
	val notiz: String? = null,
	val erledigt: Boolean = false
)
