package de.thm.smartshopping.ui.destinations.einkaufslisten.events

import de.thm.smartshopping.data.Einkaufsliste

sealed interface EinkaufslistenEvent {
	data class CreateNewEinkaufsliste(val name: String): EinkaufslistenEvent
	data class AddOldEinkaufsliste(val name: String): EinkaufslistenEvent

	data class ShowContextMenu(val einkaufsliste: Einkaufsliste): EinkaufslistenEvent
	object DismissContextMenu: EinkaufslistenEvent

	data class RenameEinkaufsliste(val einkaufsliste: Einkaufsliste, val newName: String): EinkaufslistenEvent
	data class DeleteEinkaufsliste(val einkaufsliste: Einkaufsliste): EinkaufslistenEvent

	data class ShowCreateSheet(val boolean: Boolean): EinkaufslistenEvent
}