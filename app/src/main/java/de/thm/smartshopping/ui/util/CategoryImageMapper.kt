package de.thm.smartshopping.ui.util

import androidx.annotation.DrawableRes
import de.thm.smartshopping.R

@DrawableRes
fun getCategoryImage(category: String): Int {

    return when (category.lowercase()) {

        "eier & milchprodukte" -> R.drawable.milch
        "obst" -> R.drawable.obst
        "gemüse", "gemuese" -> R.drawable.gemuese
        "fleisch" -> R.drawable.fleisch
        "getränke", "getraenke" -> R.drawable.getraenke
        "backwaren" -> R.drawable.backwaren
        "nudeln" -> R.drawable.nudeln
        "gewürze", "gewuerze" -> R.drawable.gewuerze
        "haushalt" -> R.drawable.haushaltsprodukte
        "süßwaren & snacks" -> R.drawable.suesswaren
        "tierbedarf" -> R.drawable.tierbedarf
        "konserven & fertiggerichte" -> R.drawable.konserven
        "ohne" -> R.drawable.ohne_cateogry
        else -> R.drawable.default_category
    }
}