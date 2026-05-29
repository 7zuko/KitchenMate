package de.thm.smartshopping.ui.destinations.einkaufslisten.states

import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.data.EinkaufsArtikel
import de.thm.smartshopping.data.Einkaufsliste

data class EinkaufslistenAnsichtState(
	val einkaufsliste: Einkaufsliste? = null,
	val gruppierteArtikel: List<KategorieMitEinkaufsArtikeln> = emptyList(),
	val artikelOhneKategorie: List<EinkaufsArtikel> = emptyList(),
	val isLoading: Boolean = false,

	val allKategorien: List<ArtikelKategorie> = emptyList(),
	val allArtikel: List<Artikel> = emptyList(),

	val existingEinkaufsArtikel: EinkaufsArtikel? = null,
	val showActionMenu: Boolean = false,
	val showAddArtikelMenu: Boolean = false,
	val showEditArtikelMenu: EinkaufsArtikel? = null,

	val isInSelectionMode: Boolean = false,
	val selectedArtikelIds: Set<String> = emptySet(),

	val isEnterTransitionFinished: Boolean = false
)

data class KategorieMitEinkaufsArtikeln(
	val kategorie: ArtikelKategorie,
	val einkaufsArtikelListe: List<EinkaufsArtikel>,
	val isExpanded: Boolean = false
)