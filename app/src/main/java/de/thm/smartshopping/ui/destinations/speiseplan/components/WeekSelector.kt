package de.thm.smartshopping.ui.destinations.speiseplan.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate

private val days = listOf(
    "Mo",
    "Di",
    "Mi",
    "Do",
    "Fr",
    "Sa",
    "So"
)

@RequiresApi(Build.VERSION_CODES.O)
private val monday = LocalDate.now().with(DayOfWeek.MONDAY)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekSelector(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){

        days.forEachIndexed { index, day ->

            Card(
                modifier = Modifier
                    .width(48.dp)
                    .clickable {
                        onDaySelected(index)
                    },
                colors = CardDefaults.cardColors(
                    containerColor =
                        if (selectedDay == index)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation =
                        if (selectedDay == index) 6.dp else 1.dp
                )
            ) {

                val date = monday.plusDays(index.toLong())

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelMedium,
                        color =
                            if (selectedDay == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color =
                            if (selectedDay == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                    )

                }

            }

        }

    }

}