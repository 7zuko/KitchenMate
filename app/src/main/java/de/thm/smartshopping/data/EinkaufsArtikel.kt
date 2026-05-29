package de.thm.smartshopping.data

data class EinkaufsArtikel(
	val artikel: Artikel,
	val menge: Double,
	val notiz: String? = null,
	var erledigt: Boolean = false
)
