package de.thm.smartshopping.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import de.thm.smartshopping.data.db.entity.EinkaufsArtikelCrossRef
import de.thm.smartshopping.data.db.entity.EinkaufslisteEntity
import de.thm.smartshopping.data.db.relation.EinkaufslisteWithCrossRefs
import kotlinx.coroutines.flow.Flow

@Dao
interface EinkaufslisteDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertEinkaufsliste(liste: EinkaufslisteEntity)

	@Update
	suspend fun updateEinkaufsliste(liste: EinkaufslisteEntity)

	@Delete
	suspend fun deleteEinkaufsliste(liste: EinkaufslisteEntity)

	@Query("SELECT * FROM einkaufslisten WHERE id = :id")
	suspend fun getEinkaufslisteById(id: String): EinkaufslisteEntity?


	//EinkaufsArtikel
	@Upsert
	suspend fun upsertEinkaufsArtikel(einkaufsArtikel: EinkaufsArtikelCrossRef)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAllEinkaufsArtikel(einkaufsArtikel: List<EinkaufsArtikelCrossRef>)

	@Delete
	suspend fun deleteEinkaufsArtikel(einkaufsArtikelCrossRef: EinkaufsArtikelCrossRef)

	@Query("DELETE FROM einkaufslisten_artikel WHERE einkaufslisteId = :einkaufslisteId")
	suspend fun clearArtikelForEinkaufsliste(einkaufslisteId: String)

	@Query("SELECT * FROM einkaufslisten_artikel WHERE einkaufslisteId = :einkaufslisteId")
	fun getArtikelForEinkaufsliste(einkaufslisteId: Int): Flow<List<EinkaufsArtikelCrossRef>>

	@Query("SELECT * FROM einkaufslisten_artikel WHERE einkaufslisteId = :einkaufslisteId AND artikelId = :artikelId")
	fun getEinkaufslisteArtikelById(einkaufslisteId: String, artikelId: String): Flow<EinkaufsArtikelCrossRef?>


	//Combined Queries, Einkaufsliste mit Artikeln
	@Transaction
	@Query("SELECT * FROM einkaufslisten WHERE id = :einkaufslisteId")
	fun getEinkaufslisteWithArtikelById(einkaufslisteId: String): Flow<EinkaufslisteWithCrossRefs?>

	@Transaction
	@Query("SELECT * FROM einkaufslisten ORDER BY bearbeitetAmMillis DESC")
	fun getAllEinkaufslistenWithArtikel(): Flow<List<EinkaufslisteWithCrossRefs>>
}