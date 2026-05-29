package de.thm.smartshopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.thm.smartshopping.data.datastore.ShoppingModeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class InitialCheckUiState(
	val isLoading: Boolean = true,
	val navigateToShoppingListId: String? = null,
	val navigateToDefault: Boolean = false,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
	private val shoppingModeRepository: ShoppingModeRepository,
) : ViewModel() {

	val initialCheckUiState: StateFlow<InitialCheckUiState> =
		shoppingModeRepository.activeShoppingListId.combine(
			shoppingModeRepository.isShoppingModeActive
		) { listId, isActive ->
			if (isActive && listId != null) {
				InitialCheckUiState(isLoading = false, navigateToShoppingListId = listId)
			} else {
				InitialCheckUiState(isLoading = false, navigateToDefault = true)
			}
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = InitialCheckUiState(isLoading = true)
		)
}