package de.thm.smartshopping.ui.destinations.rezepte.events

import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.Rezept
import de.thm.smartshopping.data.RezeptZutat

sealed interface RezepteEvent {

    data class CreateRezept(
        val rezept: Rezept
    ) : RezepteEvent

    data class ShowCreateSheet(
        val show: Boolean
    ) : RezepteEvent

    data class AddZutatToRezept(
        val rezeptId: String,
        val zutat: RezeptZutat
    ) : RezepteEvent

    data class ShowAddZutatSheet(
        val show: Boolean
    ) : RezepteEvent

    data class SelectArtikelForRezept(
        val artikel: Artikel
    ) : RezepteEvent

    data object ClearSelectedArtikelForRezept : RezepteEvent

    data object LoadAllArtikel : RezepteEvent

    data class RemoveZutatFromRezept(
        val rezeptId: String,
        val zutat: RezeptZutat
    ) : RezepteEvent
}