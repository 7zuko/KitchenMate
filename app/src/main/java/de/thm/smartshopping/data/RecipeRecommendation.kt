package de.thm.smartshopping.data

data class RecipeRecommendation(

    val rezept: Rezept,

    val score: Int,

    val verwendeteArtikel: List<Artikel>,

    val fehlendeArtikel: List<Artikel>,

    val reason: String

)