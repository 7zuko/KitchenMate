package de.thm.smartshopping.data

data class RezeptZutatStatus(

    val rezeptZutat: RezeptZutat,

    val bestand: Double?,

    val status: ZutatenStatus
)

enum class ZutatenStatus {

    VORHANDEN,

    TEILWEISE,

    FEHLT
}