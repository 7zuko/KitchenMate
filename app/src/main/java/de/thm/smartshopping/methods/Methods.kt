package de.thm.smartshopping.methods

import androidx.compose.ui.unit.dp
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun trigger(triggerInt: Int): Int {
	return if (triggerInt == 1) 2 else 1
}

fun Double.formatToDisplay(): String {
	val locale = Locale.GERMANY
	val numberFormat = NumberFormat.getNumberInstance(locale)

	if (numberFormat is DecimalFormat) {
		numberFormat.maximumFractionDigits = 2
		numberFormat.minimumFractionDigits = 0
	}

	return numberFormat.format(this)
}

val navBarHeight = 80.dp

val enterTransitionDuration = 400