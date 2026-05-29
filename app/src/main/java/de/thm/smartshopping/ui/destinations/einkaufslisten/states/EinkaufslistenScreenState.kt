package de.thm.smartshopping.ui.destinations.einkaufslisten.states

import de.thm.smartshopping.data.Einkaufsliste

data class EinkaufslistenScreenState(
	val einkaufslisten: List<Einkaufsliste> = emptyList(),
	val isLoading: Boolean = false,
	val selectedEinkaufslisteForMenu: Einkaufsliste? = null,
	val einkaufslisteToRename: Einkaufsliste? = null,
	val einkaufslisteToDelete: Einkaufsliste? = null,
	val showCreateSheet: Boolean = false
)