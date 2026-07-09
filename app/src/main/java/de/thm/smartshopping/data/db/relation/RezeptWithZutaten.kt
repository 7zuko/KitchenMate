package de.thm.smartshopping.data.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import de.thm.smartshopping.data.db.entity.RezeptEntity
import de.thm.smartshopping.data.db.entity.RezeptZutatEntity

data class RezeptWithZutaten(

    @Embedded
    val rezept: RezeptEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "rezeptId"
    )
    val zutaten: List<RezeptZutatEntity>

)