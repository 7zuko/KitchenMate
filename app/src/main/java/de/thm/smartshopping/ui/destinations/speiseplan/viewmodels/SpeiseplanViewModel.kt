package de.thm.smartshopping.ui.destinations.speiseplan.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.thm.smartshopping.data.db.repository.ShoppingRepository
import de.thm.smartshopping.ui.destinations.speiseplan.events.SpeiseplanEvent
import de.thm.smartshopping.data.MealPlan
import de.thm.smartshopping.ui.destinations.speiseplan.states.SpeiseplanState
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SpeiseplanViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow(
            SpeiseplanState()
        )

    val state =
        _state.asStateFlow()

    private val allRezepteFlow =
        shoppingRepository.getAllRezepte()

    private val mealPlansFlow =
        shoppingRepository.getAllMealPlans()

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

            mealPlansFlow.collect { mealPlans ->

                _state.update {

                    it.copy(
                        mealPlans = mealPlans
                    )
                }
            }
        }

        viewModelScope.launch {

            shoppingRepository
                .getRecipeRecommendations()
                .collect { recommendations ->

                    _state.update {

                        it.copy(
                            recommendations = recommendations
                        )

                    }

                }

        }
    }

    fun onEvent(
        event: SpeiseplanEvent
    ) {

        when (event) {

            is SpeiseplanEvent.SelectDay -> {

                _state.update {

                    it.copy(
                        selectedDay = event.day
                    )

                }

            }

            is SpeiseplanEvent.ShowRecipeSheet -> {

                _state.update {

                    it.copy(
                        showRecipeSheet = event.show,
                        selectedMeal = event.meal
                    )

                }

            }

            is SpeiseplanEvent.SelectRecipe -> {

                val meal =
                    _state.value.selectedMeal ?: return

                val day =
                    _state.value.selectedDay

                viewModelScope.launch {

                    shoppingRepository.saveMealPlan(

                        MealPlan(

                            id = java.util.UUID.randomUUID().toString(),

                            day = day,

                            mealType = meal,

                            rezept = event.rezept

                        )

                    )

                    _state.update {

                        it.copy(

                            showRecipeSheet = false,

                            selectedMeal = null

                        )

                    }

                }

            }

            is SpeiseplanEvent.DeleteMealPlan -> {

                viewModelScope.launch {

                    shoppingRepository.deleteMealPlan(

                        event.day,

                        event.mealType

                    )

                }

            }

            is SpeiseplanEvent.CreateShoppingListFromDay -> {

                viewModelScope.launch {

                    val day =
                        _state.value.selectedDay

                    val listId =
                        shoppingRepository.createShoppingListFromDay(day)

                    if (listId.isNotBlank()) {

                        // Später können wir hier zur Einkaufsliste navigieren
                        // oder einen Snackbar-Event senden.

                    }

                }

            }


            is SpeiseplanEvent.CreateShoppingListFromWeek -> {

                viewModelScope.launch {

                    val listId =
                        shoppingRepository.createShoppingListFromWeek()

                    if (listId.isNotBlank()) {

                        // Später Snackbar oder Navigation

                    }

                }

            }

        }

    }

}