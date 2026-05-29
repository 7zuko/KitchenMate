package de.thm.smartshopping.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "einkaufslisten")
data class EinkaufslisteEntity(
	@PrimaryKey
	val id: String,
	var name: String,
	val erstellDatumMillis: Long,
	val bearbeitetAmMillis: Long = erstellDatumMillis,
	var erledigtAmMillis: Long? = null,
	val erstellerId: String? = null
)
