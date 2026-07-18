package de.thm.smartshopping.data.db.dao

import androidx.room.*
import de.thm.smartshopping.data.db.entity.MealPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(
        mealPlan: MealPlanEntity
    )

    @Update
    suspend fun updateMealPlan(
        mealPlan: MealPlanEntity
    )

    @Delete
    suspend fun deleteMealPlan(
        mealPlan: MealPlanEntity
    )

    @Query("SELECT * FROM meal_plans")
    fun getAllMealPlans(): Flow<List<MealPlanEntity>>

    @Query(
        """
        SELECT * FROM meal_plans
        WHERE day = :day
        """
    )
    fun getMealPlansByDay(
        day: Int
    ): Flow<List<MealPlanEntity>>

    @Query(
        """
        DELETE FROM meal_plans
        WHERE day = :day
        AND mealType = :mealType
        """
    )
    suspend fun deleteMealPlan(
        day: Int,
        mealType: String
    )

}