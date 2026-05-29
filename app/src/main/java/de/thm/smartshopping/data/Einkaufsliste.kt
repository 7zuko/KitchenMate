package de.thm.smartshopping.data

import java.util.Date

data class Einkaufsliste(
	val id: String,
	var name: String,
	var artikel: MutableList<EinkaufsArtikel> = mutableListOf(),
	val erstelldatum: Date = Date(),
	var bearbeitetAm: Date = erstelldatum,
	var erledigtAm: Date? = null,
	val erstellerId: String? = null
)