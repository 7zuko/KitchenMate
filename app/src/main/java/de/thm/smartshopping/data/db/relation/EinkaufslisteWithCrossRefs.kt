package de.thm.smartshopping.data.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import de.thm.smartshopping.data.db.entity.EinkaufsArtikelCrossRef
import de.thm.smartshopping.data.db.entity.EinkaufslisteEntity

data class EinkaufslisteWithCrossRefs(
	@Embedded
	val einkaufsliste: EinkaufslisteEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "einkaufslisteId"
	)
	val einkaufsArtikelCrossRefs: List<EinkaufsArtikelCrossRef>
)
