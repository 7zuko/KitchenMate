package de.thm.smartshopping.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.thm.smartshopping.data.db.entity.ArtikelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtikelDao {

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertArtikel(artikel: ArtikelEntity)

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertAllArtikel(artikel: List<ArtikelEntity>)

	@Update
	suspend fun updateArtikel(artikel: ArtikelEntity)

	@Delete
	suspend fun deleteArtikel(artikel: ArtikelEntity)

	@Query("SELECT * FROM artikel ORDER BY name ASC")
	fun getAllArtikel(): Flow<List<ArtikelEntity>>

	@Query("SELECT * FROM artikel WHERE id = :id")
	fun getArtikelById(id: String): Flow<ArtikelEntity?>

	@Query("SELECT * FROM artikel")
	fun getAllArtikelEntities(): Flow<List<ArtikelEntity>>

	@Query("SELECT * FROM artikel WHERE name LIKE '%' || :name || '%'")
	fun getArtikelByName(name: String): Flow<List<ArtikelEntity>>
}