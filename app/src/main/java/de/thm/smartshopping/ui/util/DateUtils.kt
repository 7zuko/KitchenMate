package de.thm.smartshopping.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun formatDate(time: Long): String {

    val formatter = SimpleDateFormat(
        "dd.MM.yyyy",
        Locale.GERMANY
    )

    return formatter.format(Date(time))
}

fun tageBisAblauf(time: Long): Long {

    return TimeUnit.MILLISECONDS.toDays(
        time - System.currentTimeMillis()
    )

}

fun resttageText(time: Long): String {

    val tage = tageBisAblauf(time)

    return when {
        tage < 0 -> "Abgelaufen"
        tage == 0L -> "Heute"
        tage == 1L -> "Morgen"
        else -> "In $tage Tagen"
    }

}