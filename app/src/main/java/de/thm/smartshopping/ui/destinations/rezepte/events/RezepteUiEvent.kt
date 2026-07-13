package de.thm.smartshopping.ui.destinations.rezepte.events

sealed interface RezepteUiEvent {

    data class ShowSnackbar(
        val message: String
    ) : RezepteUiEvent

}