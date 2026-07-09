package de.thm.smartshopping.ui.destinations.artikelverwaltung.states

import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.data.VorratsArtikel

data class ArtikelVerwaltungState(

	val artikelListe: List<Artikel> = emptyList(),

	val vorratsArtikel: List<VorratsArtikel> = emptyList(),

	val gruppierteArtikel: List<KategorieMitArtikeln> = emptyList(),

	val artikelOhneKategorie: List<VorratsArtikel> = emptyList(),

	val isLoading: Boolean = false,

	val allKategorien: List<ArtikelKategorie> = emptyList(),

	val showAddArtikelMenu: Boolean = false,

	val isInSelectionMode: Boolean = false,

	val selectedArtikelIds: Set<String> = emptySet(),

	val isEnterTransitionFinished: Boolean = false,

	val currentArtikel: Artikel? = null
)

data class KategorieMitArtikeln(
	val kategorie: ArtikelKategorie,
	val artikelListe: List<VorratsArtikel>,
	val isExpanded: Boolean = false
)