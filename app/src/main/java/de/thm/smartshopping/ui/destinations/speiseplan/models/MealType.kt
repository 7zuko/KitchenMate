package de.thm.smartshopping.ui.destinations.speiseplan.models

enum class MealType(
    val title: String,
    val emoji: String
) {
    BREAKFAST(
        title = "Frühstück",
        emoji = "🍳"
    ),

    LUNCH(
        title = "Mittagessen",
        emoji = "🍝"
    ),

    DINNER(
        title = "Abendessen",
        emoji = "🌙"
    )
}