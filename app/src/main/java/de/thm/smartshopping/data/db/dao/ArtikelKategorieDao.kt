package de.thm.smartshopping.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import de.thm.smartshopping.data.db.entity.ArtikelKategorieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtikelKategorieDao {

	@Upsert
	suspend fun upsertArtikelKategorie(kategorie: ArtikelKategorieEntity)

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertAllArtikelKategorien(kategorien: List<ArtikelKategorieEntity>)

	@Delete
	suspend fun deleteArtikelKategorie(kategorie: ArtikelKategorieEntity)

	@Query("SELECT * FROM artikelkategorien ORDER BY name ASC")
	fun getAllArtikelKategorien(): Flow<List<ArtikelKategorieEntity>>

	@Query("SELECT * FROM artikelkategorien WHERE id = :id")
	fun getArtikelKategorieById(id: String): Flow<ArtikelKategorieEntity?>
}