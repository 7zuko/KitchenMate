package de.thm.smartshopping.ui.destinations.rezepte.events

import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.data.Rezept
import de.thm.smartshopping.data.RezeptZutat
import de.thm.smartshopping.ui.destinations.artikelverwaltung.events.ArtikelVerwaltungEvent

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

    data class RemoveZutatFromRezept(
        val rezeptId: String,
        val zutat: RezeptZutat
    ) : RezepteEvent

    data class CreateShoppingListFromRecipe(
        val rezept: Rezept
    ) : RezepteEvent

    data class ShowArtikelSheet(
        val show: Boolean
    ) : RezepteEvent

    data class SaveArtikel(
        val artikel: Artikel
    ) : RezepteEvent

    data class SaveKategorie(
        val kategorie: ArtikelKategorie
    ) : RezepteEvent

    data class DeleteRezept(
        val rezept: Rezept
    ) : RezepteEvent

    data class UpdateRezept(
        val rezept: Rezept
    ) : RezepteEvent

    data class AddArtikelToShoppingList(
        val einkaufsliste: Einkaufsliste,
        val zutat: RezeptZutat
    ) : RezepteEvent

    data class ShowEditImageSheet(
        val show: Boolean
    ) : RezepteEvent
}