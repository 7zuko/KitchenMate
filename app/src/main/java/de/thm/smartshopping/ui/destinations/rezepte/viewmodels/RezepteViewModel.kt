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
import de.thm.smartshopping.data.Rezept
import de.thm.smartshopping.data.VorratsArtikel
import de.thm.smartshopping.data.ZutatenStatus
import de.thm.smartshopping.ui.destinations.rezepte.events.RezepteUiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
@HiltViewModel
class RezepteViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val allVorratFlow =
        shoppingRepository.getAllVorratsArtikel()

    private val _state = MutableStateFlow(
        RezepteScreenState()
    )

    private val _uiEvent =
        MutableSharedFlow<RezepteUiEvent>()

    val uiEvent =
        _uiEvent.asSharedFlow()

    private val allRezepteFlow =
        shoppingRepository.getAllRezepte()

    private val allArtikelFlow =
        shoppingRepository.getAllArtikel()

    private val allKategorienFlow =
        shoppingRepository.getAllArtikelKategorien()

    private val allEinkaufslistenFlow =
        shoppingRepository.getAllEinkaufslisten()

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {

            combine(
                allRezepteFlow,
                allArtikelFlow,
                allKategorienFlow,
                allVorratFlow,
                allEinkaufslistenFlow
            ) { rezepte, artikel, kategorien, vorrat, einkaufslisten ->

                Quintuple(
                    rezepte,
                    artikel,
                    kategorien,
                    vorrat,
                    einkaufslisten
                )

            }.collect { (rezepte, artikel, kategorien, vorrat, einkaufslisten) ->

                _state.update {

                    it.copy(
                        rezepte = rezepte,
                        allArtikel = artikel,
                        allKategorien = kategorien,
                        einkaufslisten = einkaufslisten
                    )

                }

                updateZutatenStatus(
                    rezepte,
                    vorrat
                )
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

            is RezepteEvent.UpdateRezept -> {

                viewModelScope.launch {

                    shoppingRepository.saveRezept(
                        event.rezept
                    )

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

                    val listId =
                        shoppingRepository.createShoppingListFromRecipe(
                            event.rezept
                        )

                    if (listId.isBlank()) {

                        _uiEvent.emit(

                            RezepteUiEvent.ShowSnackbar(
                                "Alle Zutaten sind bereits im Vorrat."
                            )

                        )

                    } else {

                        _uiEvent.emit(

                            RezepteUiEvent.ShowSnackbar(
                                "Einkaufsliste wurde erstellt."
                            )

                        )

                    }

                }
            }

            is RezepteEvent.DeleteRezept -> {

                viewModelScope.launch {

                    shoppingRepository.deleteRezept(event.rezept)

                }
            }

            is RezepteEvent.AddArtikelToShoppingList -> {

                viewModelScope.launch {

                    shoppingRepository.addZutatToShoppingList(
                        event.einkaufsliste,
                        event.zutat
                    )

                }
            }

            is RezepteEvent.ShowEditImageSheet -> {

                _state.update {

                    it.copy(
                        showEditImageSheet = event.show
                    )

                }

            }
        }
    }

    private fun updateZutatenStatus(
        rezepte: List<Rezept>,
        vorrat: List<VorratsArtikel>
    ) {

        val statusMap =
            mutableMapOf<String, ZutatenStatus>()

        rezepte.forEach { rezept ->

            rezept.zutaten.forEach { zutat ->

                val lagerbestand =
                    vorrat.find {
                        it.artikel.id == zutat.artikel.id
                    }?.menge ?: 0.0

                val status =
                    when {

                        lagerbestand <= 0.0 ->
                            ZutatenStatus.FEHLT

                        lagerbestand < zutat.menge ->
                            ZutatenStatus.TEILWEISE

                        else ->
                            ZutatenStatus.VORHANDEN
                    }

                statusMap[zutat.artikel.id] = status
            }
        }

        _state.update {
            it.copy(
                zutatenStatus = statusMap
            )
        }
    }
}



private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)