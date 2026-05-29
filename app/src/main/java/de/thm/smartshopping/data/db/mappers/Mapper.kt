package de.thm.smartshopping.data.db.mappers

import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.data.EinkaufsArtikel
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.data.db.entity.ArtikelEntity
import de.thm.smartshopping.data.db.entity.ArtikelKategorieEntity
import de.thm.smartshopping.data.db.entity.EinkaufsArtikelCrossRef
import de.thm.smartshopping.data.db.entity.EinkaufslisteEntity
import de.thm.smartshopping.data.db.relation.EinkaufslisteWithCrossRefs
import java.sql.Date

fun EinkaufsArtikelCrossRef.toDomain(artikelEntity: ArtikelEntity, artikelKategorie: ArtikelKategorie?): EinkaufsArtikel {
	return EinkaufsArtikel(
		artikel = artikelEntity.toDomain(artikelKategorie),
		menge = this.menge,
		notiz = this.notiz,
		erledigt = this.erledigt
	)
}

fun EinkaufslisteWithCrossRefs.toDomain(
	artikelMap: Map<String, ArtikelEntity>,
	kategorieMap: Map<String, ArtikelKategorieEntity>
): Einkaufsliste {
	val domainArtikelList = this.einkaufsArtikelCrossRefs.mapNotNull { crossRef ->
		artikelMap[crossRef.artikelId]?.let { artikelEntity ->
			val artikelKategorie: ArtikelKategorie? = artikelEntity.kategorieId?.let { kategorieId ->
				kategorieMap[kategorieId]?.toDomain()
			}
			EinkaufsArtikel(
				artikel = artikelEntity.toDomain(artikelKategorie),
				menge = crossRef.menge,
				notiz = crossRef.notiz,
				erledigt = crossRef.erledigt
			)
		}
	}.toMutableList()

	return Einkaufsliste(
		id = this.einkaufsliste.id,
		name = this.einkaufsliste.name,
		artikel = domainArtikelList,
		erstelldatum = Date(this.einkaufsliste.erstellDatumMillis),
		bearbeitetAm = Date(this.einkaufsliste.bearbeitetAmMillis),
		erledigtAm = this.einkaufsliste.erledigtAmMillis?.let { Date(it) },
		erstellerId = this.einkaufsliste.erstellerId
	)
}

fun Artikel.toEntity(): ArtikelEntity {
	return ArtikelEntity(
		id = this.id,
		name = this.name,
		einheit = this.einheit,
		kategorieId = this.kategorie?.id
	)
}

fun ArtikelEntity.toDomain(artikelKategorie: ArtikelKategorie?): Artikel {
	return Artikel(
		id = this.id,
		name = this.name,
		kategorie = artikelKategorie,
		einheit = this.einheit
	)
}

fun ArtikelKategorie.toEntity(): ArtikelKategorieEntity {
	return ArtikelKategorieEntity(
		id = this.id,
		name = this.name
	)
}

fun ArtikelKategorieEntity.toDomain(): ArtikelKategorie {
	return ArtikelKategorie(
		id = this.id,
		name = this.name
	)
}

fun Einkaufsliste.toEntity(): EinkaufslisteEntity {
	return EinkaufslisteEntity(
		id = this.id,
		name = this.name,
		erstellDatumMillis = this.erstelldatum.time,
		bearbeitetAmMillis = this.bearbeitetAm.time,
		erledigtAmMillis = this.erledigtAm?.time,
		erstellerId = this.erstellerId
	)
}

fun List<EinkaufsArtikel>.toArtikelEntities(): List<ArtikelEntity> {
	return this.map { it.artikel.toEntity() }.distinctBy { it.id }
}

fun List<EinkaufsArtikel>.toEinkaufsArtikelEntities(einkaufslisteId: String): List<EinkaufsArtikelCrossRef> {
	return this.map {
		EinkaufsArtikelCrossRef(
			einkaufslisteId = einkaufslisteId,
			artikelId = it.artikel.id,
			menge = it.menge,
			notiz = it.notiz,
			erledigt = it.erledigt
		)
	}
}