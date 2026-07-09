package de.thm.smartshopping.data.db.dao

import androidx.room.*
import de.thm.smartshopping.data.db.entity.RezeptEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Transaction
import de.thm.smartshopping.data.db.entity.RezeptZutatEntity
import de.thm.smartshopping.data.db.relation.RezeptWithZutaten

@Dao
interface RezeptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRezept(rezept: RezeptEntity)

    @Update
    suspend fun updateRezept(rezept: RezeptEntity)

    @Delete
    suspend fun deleteRezept(rezept: RezeptEntity)

    @Query("SELECT * FROM rezepte")
    fun getAllRezepte(): Flow<List<RezeptEntity>>

    @Transaction
    @Query("SELECT * FROM rezepte")
    fun getAllRezepteWithZutaten(): Flow<List<RezeptWithZutaten>>

    @Query("SELECT * FROM rezepte WHERE id = :id")
    fun getRezeptById(id: String): Flow<RezeptEntity?>

    @Transaction
    @Query("SELECT * FROM rezepte WHERE id = :id")
    fun getRezeptWithZutatenById(
        id: String
    ): Flow<RezeptWithZutaten?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRezeptZutaten(
        zutaten: List<RezeptZutatEntity>
    )

    @Query("DELETE FROM rezept_zutaten WHERE rezeptId = :rezeptId")
    suspend fun deleteRezeptZutaten(
        rezeptId: String
    )
}