package de.thm.smartshopping.data

import de.thm.smartshopping.ui.destinations.speiseplan.models.MealType

data class MealPlan(

    val id: String,

    val day: Int,

    val mealType: MealType,

    val rezept: Rezept

)