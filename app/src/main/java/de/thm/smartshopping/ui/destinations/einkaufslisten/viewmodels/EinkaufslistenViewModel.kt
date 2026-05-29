package de.thm.smartshopping.ui.destinations.einkaufslisten.viewmodels

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.data.db.repository.ShoppingRepository
import de.thm.smartshopping.ui.destinations.einkaufslisten.events.EinkaufslistenEvent
import de.thm.smartshopping.ui.destinations.einkaufslisten.states.EinkaufslistenScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EinkaufslistenViewModel @Inject constructor(
	private val shoppingRepository: ShoppingRepository
) : ViewModel() {

	private val _state = MutableStateFlow(EinkaufslistenScreenState())
	val state: StateFlow<EinkaufslistenScreenState> = _state.asStateFlow()

	init {
		viewModelScope.launch {
			_state.update { it.copy(isLoading = true) }
			shoppingRepository.getAllEinkaufslisten()
				.collect { listen ->
					_state.update {
						it.copy(
							einkaufslisten = listen,
							isLoading = false
						)
					}
				}
		}
	}

	fun onEvent(event: EinkaufslistenEvent) {
		when (event) {
			is EinkaufslistenEvent.CreateNewEinkaufsliste -> {
				viewModelScope.launch {
					shoppingRepository.createNewEinkaufsliste(event.name, null)
				}
			}

			is EinkaufslistenEvent.AddOldEinkaufsliste -> {
				val calendar = Calendar.getInstance()
				calendar.add(Calendar.DAY_OF_YEAR, -1)
				val einkaufsliste = Einkaufsliste(
					id = UUID.randomUUID().toString(),
					name = event.name,
					erstellerId = null,
					bearbeitetAm = calendar.time
				)
				viewModelScope.launch {
					shoppingRepository.saveEinkaufsliste(einkaufsliste)
				}
			}

			is EinkaufslistenEvent.ShowContextMenu -> {
				_state.update {
					it.copy(selectedEinkaufslisteForMenu = event.einkaufsliste)
				}
			}

			EinkaufslistenEvent.DismissContextMenu -> {
				_state.update {
					it.copy(selectedEinkaufslisteForMenu = null)
				}
			}

			is EinkaufslistenEvent.RenameEinkaufsliste -> {
				if (event.newName.isNotBlank() && event.newName != event.einkaufsliste.name) {
					viewModelScope.launch {
						val updatedList = event.einkaufsliste.copy(name = event.newName)
						shoppingRepository.updateEinkaufsliste(updatedList)
					}
				}
			}

			is EinkaufslistenEvent.DeleteEinkaufsliste -> {
				viewModelScope.launch {
					shoppingRepository.deleteEinkaufsliste(event.einkaufsliste.id)
				}
			}

			is EinkaufslistenEvent.ShowCreateSheet -> {
				viewModelScope.launch {
					_state.update {
						it.copy(showCreateSheet = event.boolean)
					}
				}
			}
		}

		fun addDummyEinkaufsliste(name: String) {
			viewModelScope.launch {
				shoppingRepository.createNewEinkaufsliste(name, null)
			}
		}
	}
}