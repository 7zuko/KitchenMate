package de.thm.smartshopping.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plans")
data class MealPlanEntity(

    @PrimaryKey
    val id: String,

    val day: Int,

    val mealType: String,

    val rezeptId: String
)