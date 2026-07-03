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

    val state = _state.asStateFlow()

    fun onEvent(event: RezepteEvent) {
        when (event) {

            is RezepteEvent.ShowCreateSheet -> {
                _state.update {
                    it.copy(
                        showCreateSheet = event.show
                    )
                }
            }

            is RezepteEvent.CreateRezept -> {
                _state.update {
                    it.copy(
                        rezepte = it.rezepte + event.rezept,
                        showCreateSheet = false
                    )
                }
            }

            is RezepteEvent.AddZutatToRezept -> {

                _state.update { state ->

                    state.copy(
                        rezepte =
                            state.rezepte.map { rezept ->

                                if (rezept.id == event.rezeptId) {

                                    rezept.copy(
                                        zutaten =
                                            rezept.zutaten + event.zutat
                                    )

                                } else {
                                    rezept
                                }
                            }
                    )
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

            is RezepteEvent.LoadAllArtikel -> {

                viewModelScope.launch {

                    shoppingRepository.getAllArtikel()
                        .collect { artikelListe ->

                            _state.update {
                                it.copy(
                                    allArtikel = artikelListe
                                )
                            }
                        }
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
        }
    }
}