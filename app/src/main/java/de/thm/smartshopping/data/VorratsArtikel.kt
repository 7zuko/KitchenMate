package de.thm.smartshopping.data

data class VorratsArtikel(

    val artikel: Artikel,

    val menge: Double,

    val mindesthaltbarBis: Long? = null
)