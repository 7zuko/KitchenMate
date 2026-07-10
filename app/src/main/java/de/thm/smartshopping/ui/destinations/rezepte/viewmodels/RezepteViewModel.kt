package de.thm.smartshopping.ui.destinations.rezepte.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.thm.smartshopping.data.db.repository.ShoppingRepository
import de.thm.smartshopping.ui.destinations.rezepte.events.RezepteEvent
import de.thm.smartshopping.ui.destinations.rezepte.states.RezepteScreenState
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
@HiltViewModel
class RezepteViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        RezepteScreenState()
    )

    private val allRezepteFlow =
        shoppingRepository.getAllRezepte()

    private val allArtikelFlow =
        shoppingRepository.getAllArtikel()

    private val allKategorienFlow =
        shoppingRepository.getAllArtikelKategorien()

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {

            allRezepteFlow.collect { rezepte ->

                _state.update {
                    it.copy(
                        rezepte = rezepte
                    )
                }

            }

        }

        viewModelScope.launch {

            allKategorienFlow.collect { kategorien ->

                _state.update {
                    it.copy(
                        allKategorien = kategorien
                    )
                }

            }

        }

        viewModelScope.launch {

            allArtikelFlow.collect { artikelListe ->

                _state.update {
                    it.copy(
                        allArtikel = artikelListe
                    )
                }

            }

        }
    }

    fun onEvent(event: RezepteEvent) {
        when (event) {

            is RezepteEvent.ShowCreateSheet -> {
                _state.update {
                    it.copy(
                        showCreateSheet = event.show
                    )
                }
            }

            is RezepteEvent.ShowArtikelSheet -> {
                println("ShowArtikelSheet: ${event.show}")

                _state.update {
                    it.copy(
                        showArtikelSheet = event.show
                    )
                }
            }

            is RezepteEvent.SaveArtikel -> {

                viewModelScope.launch {

                    shoppingRepository.saveArtikel(
                        event.artikel
                    )

                    println("Artikel gespeichert: ${event.artikel.name}")

                    _state.update {
                        it.copy(
                            showArtikelSheet = false
                        )
                    }
                }
            }

            is RezepteEvent.SaveKategorie -> {

                viewModelScope.launch {

                    shoppingRepository.saveArtikelKategorie(
                        event.kategorie
                    )

                }
            }

            is RezepteEvent.CreateRezept -> {

                viewModelScope.launch {

                    shoppingRepository.saveRezept(event.rezept)

                    _state.update {
                        it.copy(
                            showCreateSheet = false
                        )
                    }

                }
            }

            is RezepteEvent.AddZutatToRezept -> {

                viewModelScope.launch {

                    val rezept = _state.value.rezepte.find {
                        it.id == event.rezeptId
                    } ?: return@launch

                    val neuesRezept = rezept.copy(
                        zutaten = rezept.zutaten + event.zutat
                    )

                    shoppingRepository.saveRezept(neuesRezept)
                }
            }

            is RezepteEvent.ShowAddZutatSheet -> {

                _state.update {
                    it.copy(
                        showAddZutatSheet = event.show
                    )
                }
            }

            is RezepteEvent.SelectArtikelForRezept -> {

                _state.update {
                    it.copy(
                        selectedArtikelForRezept = event.artikel
                    )
                }
            }

            is RezepteEvent.ClearSelectedArtikelForRezept -> {

                _state.update {
                    it.copy(
                        selectedArtikelForRezept = null
                    )
                }
            }

            is RezepteEvent.RemoveZutatFromRezept -> {

                _state.update { state ->

                    state.copy(
                        rezepte =
                            state.rezepte.map { rezept ->

                                if (rezept.id == event.rezeptId) {

                                    rezept.copy(
                                        zutaten =
                                            rezept.zutaten - event.zutat
                                    )

                                } else {
                                    rezept
                                }
                            }
                    )
                }
            }

            is RezepteEvent.CreateShoppingListFromRecipe -> {

                viewModelScope.launch {

                    shoppingRepository.createShoppingListFromRecipe(
                        event.rezept
                    )

                }
            }
        }
    }
}