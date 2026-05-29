package de.thm.smartshopping.data.db.repository

import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.data.EinkaufsArtikel
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.data.db.dao.ArtikelDao
import de.thm.smartshopping.data.db.dao.ArtikelKategorieDao
import de.thm.smartshopping.data.db.dao.EinkaufslisteDao
import de.thm.smartshopping.data.db.entity.ArtikelEntity
import de.thm.smartshopping.data.db.entity.ArtikelKategorieEntity
import de.thm.smartshopping.data.db.entity.EinkaufsArtikelCrossRef
import de.thm.smartshopping.data.db.mappers.toArtikelEntities
import de.thm.smartshopping.data.db.mappers.toDomain
import de.thm.smartshopping.data.db.mappers.toEinkaufsArtikelEntities
import de.thm.smartshopping.data.db.mappers.toEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID

class ShoppingRepository(
	private val einkaufslisteDao: EinkaufslisteDao,
	private val artikelDao: ArtikelDao,
	private val artikelKategorieDao: ArtikelKategorieDao
) {

	//Einkaufsliste

	suspend fun saveEinkaufsliste(einkaufsliste: Einkaufsliste) {
		val einkaufslisteEntity = einkaufsliste.toEntity()
		einkaufslisteDao.insertEinkaufsliste(einkaufslisteEntity)

		val artikelEntities = einkaufsliste.artikel.toArtikelEntities()
		artikelDao.insertAllArtikel(artikelEntities)

		einkaufslisteDao.clearArtikelForEinkaufsliste(einkaufsliste.id)
		val einkaufsArtikelEntities = einkaufsliste.artikel.toEinkaufsArtikelEntities(einkaufsliste.id)
		if (einkaufsArtikelEntities.isNotEmpty()) {
			einkaufslisteDao.insertAllEinkaufsArtikel(einkaufsArtikelEntities)
		}
	}

	suspend fun updateEinkaufsliste(einkaufsliste: Einkaufsliste) {
		einkaufsliste.bearbeitetAm = Date()
		val einkaufslisteEntity = einkaufsliste.toEntity()
		einkaufslisteDao.updateEinkaufsliste(einkaufslisteEntity)

		val artikelEntities = einkaufsliste.artikel.toArtikelEntities()
		artikelDao.insertAllArtikel(artikelEntities)

		einkaufslisteDao.clearArtikelForEinkaufsliste(einkaufsliste.id)
		val einkaufsArtikelEntities = einkaufsliste.artikel.toEinkaufsArtikelEntities(einkaufsliste.id)
		if (einkaufsArtikelEntities.isNotEmpty()) {
			einkaufslisteDao.insertAllEinkaufsArtikel(einkaufsArtikelEntities)
		}
	}

	suspend fun createNewEinkaufsliste(name: String, erstellerId: String?): String {
		val newEinkaufsliste = Einkaufsliste(
			id = UUID.randomUUID().toString(),
			name = name,
			erstellerId = erstellerId
		)
		saveEinkaufsliste(newEinkaufsliste)
		return newEinkaufsliste.id
	}

	suspend fun deleteEinkaufsliste(id: String) {
		einkaufslisteDao.getEinkaufslisteById(id)?.let {
			einkaufslisteDao.deleteEinkaufsliste(it)
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	fun getAllEinkaufslisten(): Flow<List<Einkaufsliste>> {
		return einkaufslisteDao.getAllEinkaufslistenWithArtikel()
			.flatMapLatest { relationsList ->
				if (relationsList.isEmpty()) {
					flowOf(emptyList())
				} else {
					val einkaufslistenDomainFlows: List<Flow<Einkaufsliste?>> = relationsList
						.map { relation ->
							getEinkaufslisteById(relation.einkaufsliste.id)
						}
					combine(einkaufslistenDomainFlows) { einkaufslisten ->
						einkaufslisten.filterNotNull().toList()
					}
				}
			}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	fun getEinkaufslisteById(id: String): Flow<Einkaufsliste?> {
		return einkaufslisteDao.getEinkaufslisteWithArtikelById(id)
			.flatMapLatest { relation ->
				if (relation == null) {
					flowOf(null)
				} else {
					val artikelIds = relation.einkaufsArtikelCrossRefs
						.map { it.artikelId }
						.distinct()

					if (artikelIds.isEmpty()) {
						flowOf(relation.toDomain(emptyMap(), emptyMap()))
					} else {
						val artikelFlows: List<Flow<ArtikelEntity?>> = artikelIds.map { artikelId ->
							artikelDao.getArtikelById(artikelId)
						}

						val combinedArtikelFlow: Flow<List<ArtikelEntity>> =
							combine(artikelFlows) { artikelEntities ->
								artikelEntities.filterNotNull()
							}

						combinedArtikelFlow.flatMapLatest { artikelEntities ->
							if (artikelEntities.isEmpty() && artikelIds.isNotEmpty()) {
								val artikelEntityMap = artikelEntities.associateBy { it.id }
								flowOf(relation.toDomain(artikelEntityMap, emptyMap()))
							} else {
								val kategorieIdsFromArtikel = artikelEntities
									.mapNotNull { it.kategorieId }
									.distinct()

								if (kategorieIdsFromArtikel.isEmpty()) {
									val artikelEntityMap = artikelEntities.associateBy { it.id }
									flowOf(relation.toDomain(artikelEntityMap, emptyMap()))
								} else {
									val kategorieEntityFlows: List<Flow<ArtikelKategorieEntity?>> =
										kategorieIdsFromArtikel.map { kategorieId ->
										artikelKategorieDao.getArtikelKategorieById(kategorieId)
									}

									val combinedKategorieEntitiesFlow: Flow<List<ArtikelKategorieEntity>> =
										combine(kategorieEntityFlows) { kategorieEntities ->
											kategorieEntities.filterNotNull()
										}

									combinedKategorieEntitiesFlow.map { kategorieEntities ->
										val artikelEntityMap = artikelEntities.associateBy { it.id }
										val kategorieEntityMap = kategorieEntities.associateBy { it.id }

										relation.toDomain(artikelEntityMap, kategorieEntityMap)
									}
								}
							}
						}
					}
				}
			}
	}

	//EinkaufsArtikel

	suspend fun saveEinkaufsArtikel(einkaufslisteId: String, artikelId: String, menge: Double, notiz: String?, erledigt: Boolean) {
		val einkaufsArtikelCrossRef = EinkaufsArtikelCrossRef(
			einkaufslisteId = einkaufslisteId,
			artikelId = artikelId,
			menge = menge,
			notiz = notiz,
			erledigt = erledigt
		)
		einkaufslisteDao.upsertEinkaufsArtikel(einkaufsArtikelCrossRef)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	fun getEinkaufsArtikelById(einkaufslisteId: String, artikelId: String): Flow<EinkaufsArtikel?> {
		return einkaufslisteDao.getEinkaufslisteArtikelById(einkaufslisteId, artikelId)
			.flatMapLatest { crossRef ->
				if (crossRef == null) {
					flowOf(null)
				} else {
					artikelDao.getArtikelById(crossRef.artikelId)
						.flatMapLatest { artikelEntity ->
							if (artikelEntity == null) {
								flowOf(null)
							} else {
								if (artikelEntity.kategorieId == null) {
									flowOf(crossRef.toDomain(artikelEntity, null))
								} else {
									getArtikelKategorieById(artikelEntity.kategorieId)
										.map { artikelKategorie ->
											crossRef.toDomain(artikelEntity, artikelKategorie)
										}
								}
							}
						}
				}
			}
	}

	suspend fun deleteEinkaufsArtikelById(einkaufslisteId: String, artikelId: String) {
		val einkaufsArtikelCrossRef = einkaufslisteDao.getEinkaufslisteArtikelById(einkaufslisteId, artikelId).firstOrNull()

		einkaufsArtikelCrossRef?.let {
			einkaufslisteDao.deleteEinkaufsArtikel(it)
		}
	}

	//Artikel

	suspend fun saveArtikel(artikel: Artikel) {
		val artikelEntity = artikel.toEntity()
		artikelDao.insertArtikel(artikelEntity)
	}

	suspend fun createNewArtikel(name: String, kategorie: ArtikelKategorie? = null, einheit: String? = null): String {
		val newArtikel = Artikel(
			id = UUID.randomUUID().toString(),
			name = name,
			kategorie = kategorie,
			einheit = einheit
		)
		saveArtikel(newArtikel)
		return newArtikel.id
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	fun getAllArtikel(): Flow<List<Artikel>> {
		return artikelDao.getAllArtikel().flatMapLatest { artikel ->
			if (artikel.isEmpty()) {
				flowOf(emptyList())
			} else {
				val artikelDomainFlows: List<Flow<Artikel?>> = artikel.map { artikelEntity ->
					if (artikelEntity.kategorieId == null) {
						flowOf(artikelEntity.toDomain(null))
					} else {
						getArtikelKategorieById(artikelEntity.kategorieId)
							.map { artikelKategorie   ->
								artikelEntity.toDomain(artikelKategorie)
							}
					}
				}
				combine(artikelDomainFlows) { artikelArray ->
					artikelArray.filterNotNull().toList()
				}
			}
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	fun getArtikelById(id: String): Flow<Artikel?> {
		return artikelDao.getArtikelById(id).flatMapLatest { artikelEntity ->
			if (artikelEntity == null) {
				flowOf(null)
			} else {
				if (artikelEntity.kategorieId == null) {
					flowOf(artikelEntity.toDomain(null))
				} else {
					getArtikelKategorieById(artikelEntity.kategorieId)
						.map { artikelKategorie ->
							artikelEntity.toDomain(artikelKategorie)
						}
				}
			}
		}
	}

	//ArtikelKategorie

	suspend fun saveArtikelKategorie(artikelKategorie: ArtikelKategorie) {
		val artikelKategorieEntity = artikelKategorie.toEntity()
		artikelKategorieDao.upsertArtikelKategorie(artikelKategorieEntity)
	}

	suspend fun createNewArtikelKategorie(name: String): String {
		val newArtikelKategorie = ArtikelKategorie(
			id = UUID.randomUUID().toString(),
			name = name
		)
		saveArtikelKategorie(newArtikelKategorie)
		return newArtikelKategorie.id
	}

	fun getAllArtikelKategorien(): Flow<List<ArtikelKategorie>> {
		return artikelKategorieDao.getAllArtikelKategorien().map { artikelKategorie ->
			artikelKategorie.map { it.toDomain() }
		}
	}

	fun getArtikelKategorieById(id: String): Flow<ArtikelKategorie?> {
		return artikelKategorieDao.getArtikelKategorieById(id).map { artikelKategorie ->
			artikelKategorie?.toDomain()
		}
	}

}