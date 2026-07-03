package de.thm.smartshopping.data

data class Rezept(
	val id: String,
	val name: String,
	val beschreibung: String? = null,
	val zubereitungszeit: Int = 0,
	val zutaten: List<RezeptZutat> = emptyList(),
	val bildPfad: String? = null
)