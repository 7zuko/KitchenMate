package de.thm.smartshopping.ui.destinations.artikelverwaltung.events

import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie

sealed interface ArtikelVerwaltungEvent {
	data class SetEnterTransitionFinished(val boolean: Boolean) : ArtikelVerwaltungEvent

	data class SaveArtikel(val artikel: Artikel) : ArtikelVerwaltungEvent
	data class EditArtikel(val artikel: Artikel) : ArtikelVerwaltungEvent
	data class DeleteArtikel(val artikel: Artikel) : ArtikelVerwaltungEvent

	data class SaveKategorie(val kategorie: ArtikelKategorie) : ArtikelVerwaltungEvent

	object GetAllKategorien : ArtikelVerwaltungEvent

	data class OnKategorieToggle(val kategorieId: String) : ArtikelVerwaltungEvent

	data class ShowAddArtikelMenu(val boolean: Boolean) : ArtikelVerwaltungEvent
}