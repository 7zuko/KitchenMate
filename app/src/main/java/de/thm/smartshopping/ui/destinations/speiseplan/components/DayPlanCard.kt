package de.thm.smartshopping.ui.destinations.speiseplan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.data.MealPlan
import de.thm.smartshopping.ui.destinations.speiseplan.models.MealType

@Composable
fun DayPlanCard(
    day: String,
    date: String,
    isToday: Boolean,
    mealPlans: List<MealPlan>,
    selectedDay: Int,
    onAddMeal: (MealType) -> Unit,
    onCreateShoppingList: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(

            containerColor = Color(0xFFF8F5F0)

        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Column {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(10.dp)
                        )

                    }

                    Spacer(Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = day,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    }

                    if (isToday) {

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {

                            Text(
                                text = "Heute",
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                        }

                    }

                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Plane Frühstück, Mittag- und Abendessen",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }



            MealType.entries.forEachIndexed { index, meal ->

                val mealPlan =
                    mealPlans.find {

                        it.day == selectedDay &&
                                it.mealType == meal

                    }

                MealRow(
                    meal = meal,
                    plannedRecipe = mealPlan?.rezept?.name,
                    duration = mealPlan?.rezept?.zubereitungszeit,
                    portions = mealPlan?.rezept?.portionen,
                    onAddClick = {
                        onAddMeal(meal)
                    }
                )

                if (index < MealType.entries.lastIndex) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(0xFFE6DED2)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FilledTonalButton(
                onClick = onCreateShoppingList,
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(
                    horizontal = 22.dp,
                    vertical = 10.dp
                )
            ) {

                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Einkaufsliste")
            }
        }


        Spacer(modifier = Modifier.height(12.dp))
    }
}