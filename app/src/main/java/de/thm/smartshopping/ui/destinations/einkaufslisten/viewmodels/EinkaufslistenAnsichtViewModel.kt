package de.thm.smartshopping.ui.destinations.einkaufslisten.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.data.EinkaufsArtikel
import de.thm.smartshopping.data.datastore.ShoppingModeRepository
import de.thm.smartshopping.data.db.repository.ShoppingRepository
import de.thm.smartshopping.ui.destinations.einkaufslisten.events.EinkaufslistenAnsichtEvent
import de.thm.smartshopping.ui.destinations.einkaufslisten.states.EinkaufslistenAnsichtState
import de.thm.smartshopping.ui.destinations.einkaufslisten.states.KategorieMitEinkaufsArtikeln
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EinkaufslistenAnsichtViewModel @Inject constructor(
	private val shoppingRepository: ShoppingRepository,
	private val shoppingModeRepository: ShoppingModeRepository
) : ViewModel() {

	private val _state = MutableStateFlow(EinkaufslistenAnsichtState())
	val state: StateFlow<EinkaufslistenAnsichtState> = _state.asStateFlow()

	fun onEvent(event: EinkaufslistenAnsichtEvent) {
		when (event) {
			is EinkaufslistenAnsichtEvent.LoadEinkaufsliste -> {
				val einkaufslisteFlow = shoppingRepository.getEinkaufslisteById(event.id)
				//val allEinkaufsArtikelFlow = shoppingRepository.getEinkaufslisteById(event.id)
				val allKategorienFlow = shoppingRepository.getAllArtikelKategorien()

				viewModelScope.launch {
					_state.update {
						it.copy(isLoading = true)
					}
					einkaufslisteFlow.combine(allKategorienFlow) { einkaufsArtikel, kategorien ->
						Pair(einkaufsArtikel?.artikel, kategorien)
					}.collect { (artikel, kategorien) ->
						if (artikel != null) {
							updateGruppierteEinkaufsArtikel(artikel, kategorien)
						}
					}
				}

				viewModelScope.launch {
					einkaufslisteFlow.collectLatest { fetchedListe ->
						_state.update {
							it.copy(
								einkaufsliste = fetchedListe,
								isLoading = false
							)
						}
					}
				}
			}

			is EinkaufslistenAnsichtEvent.ShowContextMenu -> {
				_state.update {
					it.copy(showActionMenu = true)
				}
			}

			EinkaufslistenAnsichtEvent.DismissContextMenu -> {
				_state.update {
					it.copy(showActionMenu = false)
				}
			}

			is EinkaufslistenAnsichtEvent.RenameEinkaufsliste -> {
				val currentEinkaufsliste = _state.value.einkaufsliste
				if (currentEinkaufsliste != null) {
					if (event.newName.isNotBlank() && event.newName != currentEinkaufsliste.name) {
						viewModelScope.launch {
							val updatedList = currentEinkaufsliste.copy(name = event.newName)
							shoppingRepository.updateEinkaufsliste(updatedList)
						}
					}
				}
			}

			EinkaufslistenAnsichtEvent.DeleteEinkaufsliste -> {
				val currentEinkaufsliste = _state.value.einkaufsliste
				if (currentEinkaufsliste != null) {
					viewModelScope.launch {
						shoppingRepository.deleteEinkaufsliste(currentEinkaufsliste.id)
					}
				}
			}

			EinkaufslistenAnsichtEvent.LoadAllArtikel -> {
				viewModelScope.launch {
					shoppingRepository.getAllArtikel().collectLatest { fetchedArtikel ->
						_state.update {
							it.copy(allArtikel = fetchedArtikel)
						}
					}
				}
			}

			is EinkaufslistenAnsichtEvent.ShowAddArtikelMenu -> {
				_state.update {
					it.copy(showAddArtikelMenu = event.boolean)
				}
			}

			is EinkaufslistenAnsichtEvent.AddEinkaufsArtikel -> {
				val currentEinkaufsliste = _state.value.einkaufsliste

				if (currentEinkaufsliste != null) {
					viewModelScope.launch {
						shoppingRepository.saveEinkaufsArtikel(
							einkaufslisteId = currentEinkaufsliste.id,
							artikelId = event.einkaufsArtikel.artikel.id,
							menge = event.einkaufsArtikel.menge,
							notiz = if (event.einkaufsArtikel.notiz.isNullOrBlank()) null else event.einkaufsArtikel.notiz,
							erledigt = event.einkaufsArtikel.erledigt
						)

						val wirdErledigt = !event.einkaufsArtikel.erledigt

						if (wirdErledigt) {

							shoppingRepository.addArtikelToVorrat(
								artikel = event.einkaufsArtikel.artikel,
								menge = event.einkaufsArtikel.menge
							)

						}
					}
				}
			}



			is EinkaufslistenAnsichtEvent.ShowEditArtikelMenu -> {
				_state.update {
					it.copy(showEditArtikelMenu = event.einkaufsArtikel)
				}
			}

			EinkaufslistenAnsichtEvent.DismissEditArtikelMenu -> {
				_state.update {
					it.copy(showEditArtikelMenu = null)
				}
			}

			is EinkaufslistenAnsichtEvent.GetEinkaufsArtikel -> {
				if (_state.value.einkaufsliste?.id != null) {
					viewModelScope.launch {
						shoppingRepository.getEinkaufsArtikelById(_state.value.einkaufsliste!!.id, event.artikelId).collectLatest { fetchedArtikel ->
							_state.update {
								it.copy(existingEinkaufsArtikel = fetchedArtikel)
							}
						}
					}
				}
			}

			is EinkaufslistenAnsichtEvent.ToggleArtikelSelection -> {
				val currentSelectedIds = _state.value.selectedArtikelIds.toMutableSet()
				if (currentSelectedIds.contains(event.artikelId)) {
					currentSelectedIds.remove(event.artikelId)
				} else {
					currentSelectedIds.add(event.artikelId)
				}
				val newIsInSelectionMode = if (currentSelectedIds.isEmpty()) {
					false
				} else {
					_state.value.isInSelectionMode
				}
				_state.update {
					it.copy(
						selectedArtikelIds = currentSelectedIds,
						isInSelectionMode = newIsInSelectionMode
					)
				}
			}

			EinkaufslistenAnsichtEvent.EnterSelectionMode -> {
				_state.update {
					it.copy(isInSelectionMode = true)
				}
			}

			EinkaufslistenAnsichtEvent.ExitSelectionMode -> {
				_state.update {
					it.copy(
						isInSelectionMode = false,
						selectedArtikelIds = emptySet()
					)
				}
			}

			EinkaufslistenAnsichtEvent.DeleteSelectedArtikel -> {
				val einkaufsliste = _state.value.einkaufsliste ?: return
				val idsToDelete = _state.value.selectedArtikelIds

				//val updatedArtikelList = einkaufsliste.artikel.filterNot { idsToDelete.contains(it.artikel.id) }
				//val updatedEinkaufsliste = einkaufsliste.copy(artikel = updatedArtikelList)

				viewModelScope.launch {
					idsToDelete.forEach { artikelId ->
						shoppingRepository.deleteEinkaufsArtikelById(einkaufsliste.id, artikelId)
					}
				}

				_state.update {
					it.copy(
						isInSelectionMode = false,
						selectedArtikelIds = emptySet()
					)
				}
			}

			is EinkaufslistenAnsichtEvent.LongPressArtikel -> {
				val currentSelectedIds = _state.value.selectedArtikelIds.toMutableSet()
				if (currentSelectedIds.contains(event.artikelId)) {
					currentSelectedIds.remove(event.artikelId)
				} else {
					currentSelectedIds.add(event.artikelId)
				}
				_state.update {
					it.copy(
						isInSelectionMode = currentSelectedIds.isNotEmpty(),
						selectedArtikelIds = currentSelectedIds
					)
				}
			}


			EinkaufslistenAnsichtEvent.AddDummyArtikel -> {
				viewModelScope.launch {
					shoppingRepository.createNewArtikel(
						name = "Dummy Artikel"
					)
				}
			}

			is EinkaufslistenAnsichtEvent.SetEnterTransitionFinished -> {
				_state.update {
					it.copy(isEnterTransitionFinished = event.boolean)
				}
			}

			is EinkaufslistenAnsichtEvent.OnKategorieToggle -> {
				_state.update { currentState ->
					val neueGruppierteArtikel = currentState.gruppierteArtikel.map { kategorieMitArtikel ->
						if (kategorieMitArtikel.kategorie.id == event.kategorieId) {
							kategorieMitArtikel.copy(isExpanded = !kategorieMitArtikel.isExpanded)
							} else {
							kategorieMitArtikel
						}
					}
					currentState.copy(gruppierteArtikel = neueGruppierteArtikel)
				}
			}

			EinkaufslistenAnsichtEvent.EnterShoppingMode -> {
				if (_state.value.einkaufsliste != null) {
					viewModelScope.launch {
						shoppingModeRepository.setShoppingMode(_state.value.einkaufsliste!!.id, true)
					}
				}
			}

			EinkaufslistenAnsichtEvent.ExitShoppingMode -> {
				viewModelScope.launch {
					shoppingModeRepository.clearShoppingMode()
				}
			}

			is EinkaufslistenAnsichtEvent.ToggleArtikelErledigt -> {
				val currentEinkaufsliste = _state.value.einkaufsliste

				if (currentEinkaufsliste != null) {
					viewModelScope.launch {
						shoppingRepository.saveEinkaufsArtikel(
							einkaufslisteId = _state.value.einkaufsliste!!.id,
							artikelId = event.einkaufsArtikel.artikel.id,
							menge = event.einkaufsArtikel.menge,
							notiz = event.einkaufsArtikel.notiz,
							erledigt = !event.einkaufsArtikel.erledigt,
						)
					}
				}
			}
		}
	}

	private fun updateGruppierteEinkaufsArtikel(alleEinkaufsArtikel: List<EinkaufsArtikel>, alleKategorien: List<ArtikelKategorie>) {
		val einkaufsArtikelMitKategorie = alleEinkaufsArtikel.filter { it.artikel.kategorie != null }
		val einkaufsArtikelOhneKategorie = alleEinkaufsArtikel.filter { it.artikel.kategorie == null }

		val gruppiert = alleKategorien.map { kategorie ->
			KategorieMitEinkaufsArtikeln(
				kategorie = kategorie,
				einkaufsArtikelListe = einkaufsArtikelMitKategorie.filter { it.artikel.kategorie?.id == kategorie.id },
				isExpanded = _state.value.gruppierteArtikel.find { it.kategorie.id == kategorie.id }?.isExpanded ?: false
			)
		}.filter { it.einkaufsArtikelListe.isNotEmpty() }

		_state.update {
			it.copy(
				gruppierteArtikel = gruppiert,
				artikelOhneKategorie = einkaufsArtikelOhneKategorie,
				isLoading = false
			)
		}
	}
}