package de.thm.smartshopping.ui.destinations.artikelverwaltung.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.data.VorratsArtikel
import de.thm.smartshopping.data.db.repository.ShoppingRepository
import de.thm.smartshopping.ui.destinations.artikelverwaltung.events.ArtikelVerwaltungEvent
import de.thm.smartshopping.ui.destinations.artikelverwaltung.states.ArtikelVerwaltungState
import de.thm.smartshopping.ui.destinations.artikelverwaltung.states.KategorieMitArtikeln
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtikelVerwaltungViewModel @Inject constructor(
	private val shoppingRepository: ShoppingRepository
) : ViewModel() {

	private val _state = MutableStateFlow(ArtikelVerwaltungState())
	val state: StateFlow<ArtikelVerwaltungState> = _state.asStateFlow()

	private val allArtikelFlow =
		shoppingRepository.getAllArtikel()

	private val allVorratFlow =
		shoppingRepository.getAllVorratsArtikel()

	private val allKategorienFlow = shoppingRepository.getAllArtikelKategorien()

	init {
		viewModelScope.launch {
			_state.update { it.copy(isLoading = true) }

			combine(

				allVorratFlow,

				allArtikelFlow,

				allKategorienFlow

			) { vorrat, artikel, kategorien ->

				Triple(vorrat, artikel, kategorien)

			}
				.collect { (vorrat, artikel, kategorien) ->

					updateGruppierteArtikel(

						vorrat,

						artikel,

						kategorien

					)

				}
		}
	}

	fun onEvent(event: ArtikelVerwaltungEvent) {
		when (event) {
			is ArtikelVerwaltungEvent.DeleteArtikel -> {
				viewModelScope.launch {
					shoppingRepository.deleteArtikel(event.artikel)
				}
			}

			is ArtikelVerwaltungEvent.ClearCurrentArtikel -> {
				_state.update {
					it.copy(currentArtikel = null)
				}
			}

			is ArtikelVerwaltungEvent.EditArtikel -> {
				_state.update {
					it.copy(
						currentArtikel = event.artikel,
						showAddArtikelMenu = true
					)
				}
			}

			is ArtikelVerwaltungEvent.SetCurrentArtikel -> {
				_state.update {
					it.copy(
						currentArtikel = event.artikel,
						showAddArtikelMenu = event.artikel != null
					)
				}
			}

			is ArtikelVerwaltungEvent.GetAllKategorien -> {
			viewModelScope.launch {
				shoppingRepository.getAllArtikelKategorien()
					.collect { artikelKategorien ->
						_state.update {
							it.copy(allKategorien = artikelKategorien)
						}
					}
				}
			}

			is ArtikelVerwaltungEvent.OnKategorieToggle -> {
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

			is ArtikelVerwaltungEvent.SaveArtikel -> {

				viewModelScope.launch {

					shoppingRepository.saveArtikel(event.artikel)

					_state.update {

						it.copy(
							showAddArtikelMenu = false,
							showAddVorratSheet = true,
							selectedArtikelForVorrat = event.artikel
						)

					}

				}

			}

			is ArtikelVerwaltungEvent.SaveKategorie -> {
				viewModelScope.launch {
					shoppingRepository.saveArtikelKategorie(event.kategorie)
				}
			}

			is ArtikelVerwaltungEvent.SetEnterTransitionFinished -> {
				_state.update {
					it.copy(isEnterTransitionFinished = event.boolean)
				}
			}

			is ArtikelVerwaltungEvent.ShowAddArtikelMenu -> {
				_state.update {
					it.copy(showAddArtikelMenu = event.boolean)
				}
			}

			is ArtikelVerwaltungEvent.ShowAddVorratSheet -> {
				_state.update {
					it.copy(
						showAddVorratSheet = event.show
					)
				}
			}

			is ArtikelVerwaltungEvent.SaveVorrat -> {
				viewModelScope.launch {
					shoppingRepository.setLagerbestand(
						artikelId = event.artikel.id,
						menge = event.menge
					)

					_state.update {
						it.copy(
							showAddVorratSheet = false
						)
					}
				}
			}

			is ArtikelVerwaltungEvent.SelectArtikelForVorrat -> {
				_state.update {
					it.copy(
						selectedArtikelForVorrat = event.artikel
					)
				}
			}

			is ArtikelVerwaltungEvent.ClearSelectedArtikelForVorrat -> {
				_state.update {
					it.copy(
						selectedArtikelForVorrat = null
					)
				}
			}

			is ArtikelVerwaltungEvent.DeleteVorrat -> {

				viewModelScope.launch {

					shoppingRepository.deleteLagerbestand(
						event.artikelId
					)

				}
			}

			is ArtikelVerwaltungEvent.EditVorrat -> {

				_state.update {
					it.copy(
						showEditVorratSheet = true,
						artikelZumBearbeiten = event.vorratsArtikel
					)
				}

			}

			is ArtikelVerwaltungEvent.CloseEditVorratSheet -> {

				_state.update {

					it.copy(
						showEditVorratSheet = false,
						artikelZumBearbeiten = null
					)

				}

			}

            else -> {}
        }
	}
	private fun updateGruppierteArtikel(

		vorrat: List<VorratsArtikel>,

		alleArtikel: List<Artikel>,

		alleKategorien: List<ArtikelKategorie>

	) {
		val artikelMitKategorie =
			vorrat.filter {
				it.artikel.kategorie != null
			}

		val artikelOhneKategorie =
			vorrat.filter {
				it.artikel.kategorie == null
			}

		val gruppiert = alleKategorien.map { kategorie ->
			KategorieMitArtikeln(
				kategorie = kategorie,
				artikelListe =
					artikelMitKategorie.filter {
						it.artikel.kategorie?.id == kategorie.id
					},
				isExpanded = _state.value.gruppierteArtikel.find { it.kategorie.id == kategorie.id }?.isExpanded ?: false
			)
		}.filter { it.artikelListe.isNotEmpty() }

		_state.update {
			it.copy(
				artikelListe = alleArtikel,
				vorratsArtikel = vorrat,
				gruppierteArtikel = gruppiert,
				artikelOhneKategorie = artikelOhneKategorie,
				isLoading = false
			)
		}
	}
}