package de.thm.smartshopping.ui.destinations.rezepte.states

import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.Rezept

data class RezepteScreenState(
    val rezepte: List<Rezept> = emptyList(),
    val showCreateSheet: Boolean = false,
    val showAddZutatSheet: Boolean = false,
    val selectedArtikelForRezept: Artikel? = null,
    val allArtikel: List<Artikel> = emptyList()
)