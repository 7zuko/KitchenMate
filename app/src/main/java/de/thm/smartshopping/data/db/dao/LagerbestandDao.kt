package de.thm.smartshopping.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.thm.smartshopping.data.db.entity.LagerbestandEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LagerbestandDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLagerbestand(
        lagerbestand: LagerbestandEntity
    )

    @Delete
    suspend fun deleteLagerbestand(
        lagerbestand: LagerbestandEntity
    )

    @Query("SELECT * FROM lagerbestand")
    fun getAllLagerbestand(): Flow<List<LagerbestandEntity>>

    @Query("SELECT * FROM lagerbestand WHERE artikelId = :artikelId")
    fun getLagerbestandByArtikelId(
        artikelId: String
    ): Flow<LagerbestandEntity?>
}