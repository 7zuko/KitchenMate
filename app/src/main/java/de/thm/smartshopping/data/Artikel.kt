package de.thm.smartshopping.data

data class Artikel(
	val id: String,
	var name: String,
	val kategorie: ArtikelKategorie? = null,
	var einheit: String? = null,
)