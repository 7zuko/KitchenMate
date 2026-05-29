package de.thm.smartshopping.ui.destinations.einkaufslisten.events

import de.thm.smartshopping.data.EinkaufsArtikel

sealed interface EinkaufslistenAnsichtEvent {
	data class SetEnterTransitionFinished(val boolean: Boolean) : EinkaufslistenAnsichtEvent

	data class LoadEinkaufsliste(val id: String): EinkaufslistenAnsichtEvent

	data class OnKategorieToggle(val kategorieId: String): EinkaufslistenAnsichtEvent

	data class AddEinkaufsArtikel(val einkaufsArtikel: EinkaufsArtikel): EinkaufslistenAnsichtEvent
	data class GetEinkaufsArtikel(val artikelId: String): EinkaufslistenAnsichtEvent

	data class RenameEinkaufsliste(val newName: String): EinkaufslistenAnsichtEvent
	object DeleteEinkaufsliste: EinkaufslistenAnsichtEvent

	object ShowContextMenu: EinkaufslistenAnsichtEvent
	object DismissContextMenu: EinkaufslistenAnsichtEvent

	data class ShowAddArtikelMenu(val boolean: Boolean): EinkaufslistenAnsichtEvent
	object LoadAllArtikel: EinkaufslistenAnsichtEvent

	data class ShowEditArtikelMenu(val einkaufsArtikel: EinkaufsArtikel): EinkaufslistenAnsichtEvent
	object DismissEditArtikelMenu: EinkaufslistenAnsichtEvent

	data class ToggleArtikelSelection(val artikelId: String): EinkaufslistenAnsichtEvent
	object EnterSelectionMode: EinkaufslistenAnsichtEvent
	object ExitSelectionMode: EinkaufslistenAnsichtEvent
	object DeleteSelectedArtikel: EinkaufslistenAnsichtEvent
	data class LongPressArtikel(val artikelId: String): EinkaufslistenAnsichtEvent

	object EnterShoppingMode: EinkaufslistenAnsichtEvent
	object ExitShoppingMode: EinkaufslistenAnsichtEvent
	data class ToggleArtikelErledigt(val einkaufsArtikel: EinkaufsArtikel): EinkaufslistenAnsichtEvent

	object AddDummyArtikel: EinkaufslistenAnsichtEvent
}