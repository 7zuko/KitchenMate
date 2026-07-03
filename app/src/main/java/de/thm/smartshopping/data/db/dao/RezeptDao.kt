package de.thm.smartshopping.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.thm.smartshopping.data.db.entity.RezeptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RezeptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRezept(
        rezept: RezeptEntity
    )

    @Query("SELECT * FROM RezeptEntity")
    fun getAllRezepte(): Flow<List<RezeptEntity>>

    @Delete
    suspend fun deleteRezept(
        rezept: RezeptEntity
    )
}