package de.thm.smartshopping.data.db.repository

import android.util.Log
import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.data.EinkaufsArtikel
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.data.Rezept
import de.thm.smartshopping.data.RezeptZutat
import de.thm.smartshopping.data.VorratsArtikel
import de.thm.smartshopping.data.db.dao.ArtikelDao
import de.thm.smartshopping.data.db.dao.ArtikelKategorieDao
import de.thm.smartshopping.data.db.dao.EinkaufslisteDao
import de.thm.smartshopping.data.db.dao.LagerbestandDao
import de.thm.smartshopping.data.db.dao.RezeptDao
import de.thm.smartshopping.data.db.entity.RezeptEntity
import de.thm.smartshopping.data.db.mappers.toEntity
import de.thm.smartshopping.data.db.mappers.toDomain
import de.thm.smartshopping.data.db.entity.ArtikelEntity
import de.thm.smartshopping.data.db.entity.ArtikelKategorieEntity
import de.thm.smartshopping.data.db.entity.EinkaufsArtikelCrossRef
import de.thm.smartshopping.data.db.entity.LagerbestandEntity
import de.thm.smartshopping.data.db.mappers.toArtikelEntities
import de.thm.smartshopping.data.db.mappers.toEinkaufsArtikelEntities
import de.thm.smartshopping.data.db.mappers.toRezeptZutatEntities
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class ShoppingRepository(
	private val einkaufslisteDao: EinkaufslisteDao,
	private val artikelDao: ArtikelDao,
	private val artikelKategorieDao: ArtikelKategorieDao,
	private val rezeptDao: RezeptDao,
	private val lagerbestandDao: LagerbestandDao
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

	suspend fun deleteArtikel(artikel: Artikel) {
		artikelDao.deleteArtikel(
			ArtikelEntity(
				id = artikel.id,
				name = artikel.name,
				einheit = artikel.einheit,
				kategorieId = artikel.kategorie?.id,
				emoji = artikel.emoji
			)
		)
	}

	suspend fun setLagerbestand(
		artikelId: String,
		menge: Double
	) {

		lagerbestandDao.upsertLagerbestand(
			LagerbestandEntity(
				artikelId = artikelId,
				menge = menge
			)
		)

	}

	suspend fun addArtikelToVorrat(
		artikel: Artikel,
		menge: Double
	) {

		val vorhandenerBestand =
			getLagerbestandByArtikelId(artikel.id)
				.firstOrNull()

		val neueMenge =
			(vorhandenerBestand?.menge ?: 0.0) + menge

		setLagerbestand(
			artikelId = artikel.id,
			menge = neueMenge
		)
	}

	fun getAllLagerbestand() =
		lagerbestandDao.getAllLagerbestand()

	fun getLagerbestandByArtikelId(
		artikelId: String
	) =
		lagerbestandDao.getLagerbestandByArtikelId(
			artikelId
		)

	suspend fun deleteLagerbestand(
		artikelId: String
	) {

		lagerbestandDao
			.getLagerbestandByArtikelId(artikelId)
			.firstOrNull()
			?.let {

				lagerbestandDao.deleteLagerbestand(it)

			}

	}

	@OptIn(ExperimentalCoroutinesApi::class)
	fun getAllVorratsArtikel(): Flow<List<VorratsArtikel>> {

		return lagerbestandDao
			.getAllLagerbestand()
			.flatMapLatest { lagerListe ->

				if (lagerListe.isEmpty()) {

					flowOf(emptyList())

				} else {

					val artikelFlows = lagerListe.map { lagerbestand ->

						artikelDao
							.getArtikelById(lagerbestand.artikelId)
							.flatMapLatest { artikelEntity ->

								if (artikelEntity == null) {

									flowOf(null)

								} else {

									if (artikelEntity.kategorieId == null) {

										flowOf(
											VorratsArtikel(
												artikel = artikelEntity.toDomain(null),
												menge = lagerbestand.menge
											)
										)

									} else {

										getArtikelKategorieById(
											artikelEntity.kategorieId
										).map { kategorie ->

											VorratsArtikel(
												artikel = artikelEntity.toDomain(kategorie),
												menge = lagerbestand.menge
											)

										}

									}

								}

							}

					}

					combine(artikelFlows) {

						it.filterNotNull()

					}

				}

			}

	}
// Rezepte

	suspend fun saveRezept(rezept: Rezept) {

		rezeptDao.insertRezept(
			rezept.toEntity()
		)

		rezeptDao.deleteRezeptZutaten(
			rezept.id
		)

		rezeptDao.insertRezeptZutaten(
			rezept.toRezeptZutatEntities()
		)

		rezept.zutaten.forEach {
			Log.d("REZEPT", "${it.artikel.name} ${it.menge}")
		}
	}

	suspend fun updateRezept(rezept: Rezept) {
		rezeptDao.updateRezept(rezept.toEntity())
	}

	suspend fun deleteRezept(rezept: Rezept) {
		rezeptDao.deleteRezept(rezept.toEntity())
	}

	fun getAllRezepte(): Flow<List<Rezept>> {
		return combine(
			rezeptDao.getAllRezepteWithZutaten(),
			getAllArtikel()
		) { rezeptRelations, artikelListe ->
			Log.d("REZEPT", "Rezepte: ${rezeptRelations.size}")

			rezeptRelations.forEach { relation ->
				Log.d(
					"REZEPT",
					"${relation.rezept.name} -> ${relation.zutaten.size} Zutaten"
				)
			}
			rezeptRelations.map { relation ->
				Rezept(
					id = relation.rezept.id,
					name = relation.rezept.name,
					beschreibung = relation.rezept.beschreibung,
					zubereitungszeit = relation.rezept.zubereitungszeit,
					portionen = relation.rezept.portionen,
					schwierigkeit = relation.rezept.schwierigkeit,
					kategorie = relation.rezept.kategorie,
					bildPfad = relation.rezept.bildPfad,
					zutaten =
						relation.zutaten.mapNotNull { zutatEntity ->
							val artikel = artikelListe.find {
								it.id == zutatEntity.artikelId
							}
							if (artikel == null) {
								null
							} else {
								de.thm.smartshopping.data.RezeptZutat(
									artikel = artikel,
									menge = zutatEntity.menge
								)
							}
						}
				)
			}
		}
	}

	fun getRezeptById(id: String): Flow<Rezept?> {
		return rezeptDao
			.getRezeptById(id)
			.map { entity ->
				entity?.toDomain()
			}
	}

	suspend fun createShoppingListFromRecipe(
		rezept: Rezept
	): String {
		val vorrat =
			getAllVorratsArtikel()
				.first()

		val fehlendeArtikel =
			rezept.zutaten.mapNotNull { zutat ->

				val lagerbestand =
					vorrat.find {
						it.artikel.id == zutat.artikel.id
					}?.menge ?: 0.0

				when {

					lagerbestand >= zutat.menge ->
						null

					lagerbestand > 0.0 ->
						EinkaufsArtikel(
							artikel = zutat.artikel,
							menge = zutat.menge - lagerbestand
						)

					else ->
						EinkaufsArtikel(
							artikel = zutat.artikel,
							menge = zutat.menge
						)
				}
			}

		if (fehlendeArtikel.isEmpty()) {
			return ""
		}

		val listId =
			createNewEinkaufsliste(
				name = rezept.name,
				erstellerId = null
			)

		val einkaufsliste =
			getEinkaufslisteById(listId)
				.firstOrNull()

		einkaufsliste?.let {

			val neueListe =
				it.copy(
					artikel = fehlendeArtikel.toMutableList()
				)

			updateEinkaufsliste(neueListe)
		}

		return listId
	}

	suspend fun addZutatToShoppingList(
		einkaufsliste: Einkaufsliste,
		zutat: RezeptZutat
	) {

		val aktuelleListe =
			getEinkaufslisteById(einkaufsliste.id)
				.firstOrNull()
				?: return

		val vorhandenerArtikel =
			aktuelleListe.artikel.find {
				it.artikel.id == zutat.artikel.id
			}

		val neueArtikel =
			aktuelleListe.artikel.toMutableList()

		if (vorhandenerArtikel != null) {

			neueArtikel.remove(vorhandenerArtikel)

			neueArtikel.add(
				vorhandenerArtikel.copy(
					menge =
						vorhandenerArtikel.menge +
								zutat.menge
				)
			)

		} else {

			neueArtikel.add(
				EinkaufsArtikel(
					artikel = zutat.artikel,
					menge = zutat.menge
				)
			)
		}

		updateEinkaufsliste(
			aktuelleListe.copy(
				artikel = neueArtikel
			)
		)
	}

}