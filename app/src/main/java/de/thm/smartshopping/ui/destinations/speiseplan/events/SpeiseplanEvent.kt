package de.thm.smartshopping.ui.destinations.speiseplan.events

import de.thm.smartshopping.data.Rezept
import de.thm.smartshopping.ui.destinations.speiseplan.models.MealType

sealed interface SpeiseplanEvent {

    data class SelectDay(
        val day: Int
    ) : SpeiseplanEvent

    data class ShowRecipeSheet(
        val show: Boolean,
        val meal: MealType? = null
    ) : SpeiseplanEvent

    data class SelectRecipe(
        val rezept: Rezept
    ) : SpeiseplanEvent

    data class DeleteMealPlan(

        val day: Int,

        val mealType: MealType

    ) : SpeiseplanEvent

    object CreateShoppingListFromDay : SpeiseplanEvent

    object CreateShoppingListFromWeek : SpeiseplanEvent


}