package de.thm.smartshopping.ui.destinations.artikelverwaltung.events

import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.data.VorratsArtikel

sealed interface ArtikelVerwaltungEvent {
	data class SetEnterTransitionFinished(val boolean: Boolean) : ArtikelVerwaltungEvent

	data class SaveArtikel(val artikel: Artikel) : ArtikelVerwaltungEvent

	data class EditArtikel(val artikel: Artikel) : ArtikelVerwaltungEvent

	data class SetCurrentArtikel(val artikel: Artikel?) : ArtikelVerwaltungEvent
	data class DeleteArtikel(val artikel: Artikel) : ArtikelVerwaltungEvent

	data class SaveKategorie(val kategorie: ArtikelKategorie) : ArtikelVerwaltungEvent

	object ClearCurrentArtikel : ArtikelVerwaltungEvent

	object GetAllKategorien : ArtikelVerwaltungEvent

	data class OnKategorieToggle(val kategorieId: String) : ArtikelVerwaltungEvent

	data class ShowAddArtikelMenu(val boolean: Boolean) : ArtikelVerwaltungEvent

	data class ToggleArtikelSelection(val artikelId: String) : ArtikelVerwaltungEvent

	data class LongPressArtikel(val artikelId: String) : ArtikelVerwaltungEvent

	object ClearSelection : ArtikelVerwaltungEvent

	object DeleteSelectedArtikel : ArtikelVerwaltungEvent

	data class ShowAddVorratSheet(
		val show: Boolean
	) : ArtikelVerwaltungEvent

	data class SaveVorrat(
		val artikel: Artikel,
		val menge: Double
	) : ArtikelVerwaltungEvent

	data class SelectArtikelForVorrat(
		val artikel: Artikel
	) : ArtikelVerwaltungEvent

	object ClearSelectedArtikelForVorrat
		: ArtikelVerwaltungEvent

	data class DeleteVorrat(
		val artikelId: String
	) : ArtikelVerwaltungEvent

	data class EditVorrat(
		val vorratsArtikel: VorratsArtikel
	) : ArtikelVerwaltungEvent

	object CloseEditVorratSheet : ArtikelVerwaltungEvent
}