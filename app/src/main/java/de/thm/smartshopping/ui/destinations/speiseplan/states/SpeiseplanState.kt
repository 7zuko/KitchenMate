package de.thm.smartshopping.ui.destinations.speiseplan.states

import de.thm.smartshopping.data.Rezept
import de.thm.smartshopping.data.MealPlan
import de.thm.smartshopping.data.RecipeRecommendation
import de.thm.smartshopping.ui.destinations.speiseplan.models.MealType

data class SpeiseplanState(

    val rezepte: List<Rezept> = emptyList(),

    val mealPlans: List<MealPlan> = emptyList(),

    val selectedDay: Int = 0,

    val showRecipeSheet: Boolean = false,

    val selectedMeal: MealType? = null,

    val recommendations: List<RecipeRecommendation> = emptyList()
)
