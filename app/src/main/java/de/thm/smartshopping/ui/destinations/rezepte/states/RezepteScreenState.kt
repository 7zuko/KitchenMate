package de.thm.smartshopping.ui.destinations.rezepte.states

import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.data.Rezept
import de.thm.smartshopping.data.RezeptZutatStatus
import de.thm.smartshopping.data.ZutatenStatus

data class RezepteScreenState(
    val rezepte: List<Rezept> = emptyList(),

    val showCreateSheet: Boolean = false,

    val showAddZutatSheet: Boolean = false,

    val selectedArtikelForRezept: Artikel? = null,

    val allArtikel: List<Artikel> = emptyList(),

    val allKategorien: List<ArtikelKategorie> = emptyList(),

    val showArtikelSheet: Boolean = false,

    val showEditImageSheet: Boolean = false,

    val zutatenStatus: Map<String, ZutatenStatus> = emptyMap(),

    val einkaufslisten: List<Einkaufsliste> = emptyList(),
)