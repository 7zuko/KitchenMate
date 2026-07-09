package de.thm.smartshopping.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import de.thm.smartshopping.data.db.converters.DateConverter
import de.thm.smartshopping.data.db.dao.ArtikelDao
import de.thm.smartshopping.data.db.dao.ArtikelKategorieDao
import de.thm.smartshopping.data.db.dao.EinkaufslisteDao
import de.thm.smartshopping.data.db.dao.LagerbestandDao
import de.thm.smartshopping.data.db.entity.ArtikelEntity
import de.thm.smartshopping.data.db.entity.ArtikelKategorieEntity
import de.thm.smartshopping.data.db.entity.EinkaufsArtikelCrossRef
import de.thm.smartshopping.data.db.entity.EinkaufslisteEntity
import de.thm.smartshopping.data.db.entity.RezeptZutatEntity
import de.thm.smartshopping.data.db.entity.LagerbestandEntity
import de.thm.smartshopping.data.db.dao.RezeptDao
import de.thm.smartshopping.data.db.entity.RezeptEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
	entities = [
		EinkaufslisteEntity::class,
		ArtikelEntity::class,
		ArtikelKategorieEntity::class,
		EinkaufsArtikelCrossRef::class,
		RezeptEntity::class,
		RezeptZutatEntity::class,
		LagerbestandEntity::class
	],
	version = 6,
	exportSchema = true
)
@TypeConverters(
	value = [
		DateConverter::class
	]
)
abstract class AppDatabase: RoomDatabase() {

	abstract fun einkaufslisteDao(): EinkaufslisteDao
	abstract fun artikelDao(): ArtikelDao
	abstract fun artikelKategorieDao(): ArtikelKategorieDao
	abstract fun rezeptDao(): RezeptDao
	abstract fun lagerbestandDao(): LagerbestandDao

	@Volatile
	private var INSTANCE: AppDatabase? = null

	fun getDatabase(context: Context): AppDatabase {
		return INSTANCE ?: synchronized(this) {
			val instance = Room.databaseBuilder(
				context.applicationContext,
				AppDatabase::class.java,
				"smart_shopping_db"
			).fallbackToDestructiveMigration().build()
			INSTANCE = instance
			instance
		}
	}

	companion object {
		@Volatile
		private var INSTANCE: AppDatabase? = null

		fun getDatabaseForCallback(context: Context): AppDatabase {
			return Room.databaseBuilder(context, AppDatabase::class.java, "smart_shopping_db")
				.addCallback(AppDatabaseCallback(context))
				.build()
		}
	}
	internal class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
		override fun onCreate(db: SupportSQLiteDatabase) {
			super.onCreate(db)
			Log.d("AppDatabaseCallback", "Database onCreate triggered...")
			CoroutineScope(Dispatchers.IO).launch {
				populateDatabase(AppDatabase.getDatabaseForCallback(context))
			}
		}

		suspend fun populateDatabase(database: AppDatabase) {
			val artikelKategorieDao = database.artikelKategorieDao()
			val artikelDao = database.artikelDao()
			val einkaufslisteDao = database.einkaufslisteDao()
			val lagerbestandDao = database.lagerbestandDao()
			val rezeptDao = database.rezeptDao()

			Log.d("AppDatabaseCallback", "Starting data population within populateDatabase...")

			// --- Sample Categories ---
			val katIdObst = UUID.randomUUID().toString()
			val katIdGemuese = UUID.randomUUID().toString()
			val katIdMilchprodukte = UUID.randomUUID().toString()
			val katIdBackwaren = UUID.randomUUID().toString()
			val katIdFleisch = UUID.randomUUID().toString()
			val katIdGetraenke = UUID.randomUUID().toString()
			val katIdSuesswaren = UUID.randomUUID().toString()
			val katIdHaushalt = UUID.randomUUID().toString()
			val katIdKonserven = UUID.randomUUID().toString()
			val katIdTierbedarf = UUID.randomUUID().toString()


			val categories = listOf(
				ArtikelKategorieEntity(id = katIdObst, name = "Obst", emoji = "\uD83C\uDF4E"),
				ArtikelKategorieEntity(id = katIdGemuese, name = "Gemüse", emoji = "\uD83E\uDD55"),
				ArtikelKategorieEntity(id = katIdMilchprodukte, name = "Eier & Milchprodukte", emoji = "\uD83E\uDD5B"),
				ArtikelKategorieEntity(id = katIdBackwaren, name = "Backwaren", emoji = "\uD83E\uDD56"),
				ArtikelKategorieEntity(id = katIdFleisch, name = "Fleisch", emoji = "\uD83E\uDD69"),
				ArtikelKategorieEntity(id = katIdGetraenke, name = "Getränke", emoji = "\uD83E\uDD64"),
				ArtikelKategorieEntity(id = katIdSuesswaren, name = "Süßwaren & Snacks", emoji = "\uD83C\uDF6B"),
				ArtikelKategorieEntity(id = katIdHaushalt, name = "Haushalt", emoji = "\uD83E\uDDFD"),
				ArtikelKategorieEntity(id = katIdKonserven, name = "Konserven & Fertiggerichte", emoji = "\uD83E\uDD6B"),
				ArtikelKategorieEntity(id = katIdTierbedarf, name = "Tierbedarf", emoji = "\uD83D\uDC31")
			)
			artikelKategorieDao.insertAllArtikelKategorien(categories)
			Log.d("AppDatabaseCallback", "${categories.size} categories inserted.")

			// --- Sample Articles ---
			// Obst
			val artikelIdApfel = UUID.randomUUID().toString()
			val artikelIdBanane = UUID.randomUUID().toString()
			val artikelIdOrangen = UUID.randomUUID().toString()
			// Gemüse
			val artikelIdTomate = UUID.randomUUID().toString()
			val artikelIdGurke = UUID.randomUUID().toString()
			val artikelIdPaprika = UUID.randomUUID().toString()
			val artikelIdZwiebel = UUID.randomUUID().toString()
			// Milchprodukte
			val artikelIdMilch = UUID.randomUUID().toString()
			val artikelIdJoghurt = UUID.randomUUID().toString()
			val artikelIdEier = UUID.randomUUID().toString()
			val artikelIdKaese = UUID.randomUUID().toString()
			// Backwaren
			val artikelIdBrot = UUID.randomUUID().toString()
			val artikelIdBroetchen = UUID.randomUUID().toString()
			val artikelIdToast = UUID.randomUUID().toString()
			// Fleisch
			val artikelIdHackfleisch = UUID.randomUUID().toString()
			val artikelIdHaehnchen = UUID.randomUUID().toString()
			val artikelIdWurst = UUID.randomUUID().toString()
			// Getränke
			val artikelIdWasser = UUID.randomUUID().toString()
			val artikelIdCola = UUID.randomUUID().toString()
			val artikelIdOrangensaft = UUID.randomUUID().toString()
			val artikelIdKaffee = UUID.randomUUID().toString()
			// Süßwaren
			val artikelIdSchokolade = UUID.randomUUID().toString()
			val artikelIdChips = UUID.randomUUID().toString()
			val artikelIdGummibaerchen = UUID.randomUUID().toString()
			// Haushalt
			val artikelIdWaschmittel = UUID.randomUUID().toString()
			val artikelIdKlopapier = UUID.randomUUID().toString()
			val artikelIdSpuelmittel = UUID.randomUUID().toString()
			// Konserven
			val artikelIdMais = UUID.randomUUID().toString()
			val artikelIdThunfisch = UUID.randomUUID().toString()
			// Tierbedarf
			val artikelIdKatzenfutter = UUID.randomUUID().toString()


			val articles = listOf(
				ArtikelEntity(id = artikelIdApfel, name = "Äpfel", einheit = "Stk", kategorieId = katIdObst, emoji = "\uD83C\uDF4E"),
				ArtikelEntity(id = artikelIdBanane, name = "Bananen", einheit = "Stk", kategorieId = katIdObst, emoji = "\uD83C\uDF4C"),
				ArtikelEntity(id = artikelIdOrangen, name = "Orangen", einheit = "Stk", kategorieId= katIdObst, emoji = "\uD83C\uDF4A"),

				ArtikelEntity(id = artikelIdTomate, name = "Tomaten", einheit = "Gramm", kategorieId = katIdGemuese, emoji = "\uD83C\uDF45"),
				ArtikelEntity(id = artikelIdGurke, name = "Gurken", einheit = "Stk", kategorieId = katIdGemuese, emoji = "\uD83E\uDD52"),
				ArtikelEntity(id = artikelIdPaprika, name = "Paprika", einheit = "Gramm", kategorieId = katIdGemuese, emoji = "\uD83E\uDED1"),
				ArtikelEntity(id = artikelIdZwiebel, name = "Zwiebelen", einheit = "Gramm", kategorieId = katIdGemuese, emoji = "\uD83E\uDDC5"),

				ArtikelEntity(id = artikelIdMilch, name = "Vollmilch (3.5%)", einheit = "Liter", kategorieId = katIdMilchprodukte, emoji = "\uD83E\uDD5B"),
				ArtikelEntity(id = artikelIdJoghurt, name = "Joghurt (1.5%)", einheit = "Gramm", kategorieId = katIdMilchprodukte, emoji = "\uD83E\uDD5B"),
				ArtikelEntity(id = artikelIdEier, name = "Eier (Größe S, Bio)", einheit = "Stk", kategorieId = katIdMilchprodukte, emoji = "\uD83E\uDD5A"),
				ArtikelEntity(id = artikelIdKaese, name = "Gouda", einheit = "Gramm", kategorieId = katIdMilchprodukte, emoji = "\uD83E\uDDC0"),

				ArtikelEntity(id = artikelIdBrot, name = "Brot", einheit = "Gramm", kategorieId = katIdBackwaren, emoji = "\uD83E\uDD56"),
				ArtikelEntity(id = artikelIdBroetchen, name = "Aufbackbrötchen", einheit = "Stk", kategorieId = katIdBackwaren, emoji = "\uD83E\uDD56"),
				ArtikelEntity(id = artikelIdToast, name = "Vollkorntoast", einheit = "Gramm", kategorieId = katIdBackwaren, emoji = "\uD83C\uDF5E"),

				ArtikelEntity(id = artikelIdHackfleisch, name = "Hackfleisch", einheit = "Gramm", kategorieId = katIdFleisch, emoji = "\uD83E\uDD69"),
				ArtikelEntity(id = artikelIdHaehnchen, name = "Hähnchenbrustfilet (Bio)", einheit = "Gramm", kategorieId = katIdFleisch, emoji = "\uD83C\uDF57"),
				ArtikelEntity(id = artikelIdWurst, name = "Salami (am Stück)", einheit = "Gramm", kategorieId = katIdFleisch, emoji = "\uD83E\uDD69"),

				ArtikelEntity(id = artikelIdWasser, name = "Mineralwasser (Still)", einheit = "Liter", kategorieId = katIdGetraenke, emoji = "\uD83D\uDEB0"),
				ArtikelEntity(id = artikelIdCola, name = "Cola Zero", einheit = "Liter", kategorieId = katIdGetraenke, emoji = "\uD83E\uDD64"),
				ArtikelEntity(id = artikelIdOrangensaft, name = "Apfelsaft", einheit = "Liter", kategorieId = katIdGetraenke, emoji = "\uD83E\uDDC3"),
				ArtikelEntity(id = artikelIdKaffee, name = "Kaffeebohnen", einheit = "Gramm", kategorieId = katIdGetraenke, emoji = "☕\uFE0F"),

				ArtikelEntity(id = artikelIdSchokolade, name = "Schokolade", einheit = "Gramm", kategorieId = katIdSuesswaren, emoji = "\uD83C\uDF6B"),
				ArtikelEntity(id = artikelIdChips, name = "Chipspackung", einheit = "Stk", kategorieId = katIdSuesswaren, emoji = "\uD83E\uDD54"),
				ArtikelEntity(id = artikelIdGummibaerchen, name = "Gummibärchen", einheit = "Gramm", kategorieId = katIdSuesswaren, emoji = "\uD83C\uDF6C"),

				ArtikelEntity(id = artikelIdWaschmittel, name = "Waschmittel", einheit = "", kategorieId = katIdHaushalt, emoji = "\uD83D\uDC5A"),
				ArtikelEntity(id = artikelIdKlopapier, name = "Toilettenpapier", einheit = "Rollen", kategorieId = katIdHaushalt, emoji = "\uD83E\uDDFB"),
				ArtikelEntity(id = artikelIdSpuelmittel, name = "Spülmittel", einheit = "ml", kategorieId = katIdHaushalt, emoji = "\uD83C\uDF7D\uFE0F"),

				ArtikelEntity(id = artikelIdMais, name = "Mais", einheit = "Gramm", kategorieId = katIdKonserven, emoji = "\uD83E\uDD6B"),
				ArtikelEntity(id = artikelIdThunfisch, name = "Thunfisch", einheit = "Gramm", kategorieId = katIdKonserven, emoji = "\uD83E\uDD6B"),

				ArtikelEntity(id = artikelIdKatzenfutter, name = "Katzenfutter", einheit = "Gramm", kategorieId = katIdTierbedarf, emoji = "\uD83D\uDE3C")
			)
			artikelDao.insertAllArtikel(articles)
			Log.d("AppDatabaseCallback", "${articles.size} articles inserted.")


			// --- Sample Rezepte ---
			val rezeptIdSpaghetti = UUID.randomUUID().toString()
			val rezeptIdBurger = UUID.randomUUID().toString()
			val rezeptIdPancakes = UUID.randomUUID().toString()
			val rezeptIdRuehrei = UUID.randomUUID().toString()
			val rezeptIdSalat = UUID.randomUUID().toString()




			// --- Sample Shopping Lists ---
			val currentTimeMillis = System.currentTimeMillis()
			val userDemoId = "demoUser123" // Example User ID

			val listeIdWocheneinkauf = UUID.randomUUID().toString()
			val listeIdWochenende = UUID.randomUUID().toString()
			val listeIdParty = UUID.randomUUID().toString()
			val listeIdSchnell = UUID.randomUUID().toString() // An already completed list

			// Inserting EinkaufslisteEntity one by one as per your DAO
			val einkaufslisteWochen = EinkaufslisteEntity(
				id = listeIdWocheneinkauf, name = "Wocheneinkauf KW 23",
				erstellDatumMillis = currentTimeMillis - (5 * 24 * 60 * 60 * 1000), // 5 days ago
				bearbeitetAmMillis = currentTimeMillis - (1 * 24 * 60 * 60 * 1000), // 1 day ago
				erstellerId = userDemoId
			)
			val einkaufslisteWE = EinkaufslisteEntity(
				id = listeIdWochenende, name = "Einkauf Wochenende",
				erstellDatumMillis = currentTimeMillis - (2 * 24 * 60 * 60 * 1000), // 2 days ago
				bearbeitetAmMillis = currentTimeMillis, // Just now
				erstellerId = userDemoId
			)
			val einkaufslisteParty = EinkaufslisteEntity(
				id = listeIdParty, name = "Grillparty Vorbereitung",
				erstellDatumMillis = currentTimeMillis - (10 * 24 * 60 * 60 * 1000), // 10 days ago
				bearbeitetAmMillis = currentTimeMillis - (3 * 24 * 60 * 60 * 1000), // 3 days ago
				erstellerId = userDemoId
			)
			val einkaufslisteSchnell = EinkaufslisteEntity(
				id = listeIdSchnell, name = "Schnell was holen (Erledigt)",
				erstellDatumMillis = currentTimeMillis - (1 * 24 * 60 * 60 * 1000), // 1 day ago
				bearbeitetAmMillis = currentTimeMillis - (23 * 60 * 60 * 1000),   // 23 hours ago
				erledigtAmMillis = currentTimeMillis - (22 * 60 * 60 * 1000),    // Marked done 22 hours ago
				erstellerId = userDemoId
			)
			einkaufslisteDao.insertEinkaufsliste(einkaufslisteWochen)
			einkaufslisteDao.insertEinkaufsliste(einkaufslisteWE)
			einkaufslisteDao.insertEinkaufsliste(einkaufslisteParty)
			einkaufslisteDao.insertEinkaufsliste(einkaufslisteSchnell)
			Log.d("AppDatabaseCallback", "4 shopping lists inserted individually.")

			// --- Add Articles to Shopping Lists (Many-to-Many using CrossRef) ---
			val listArtikelCrossRefs = listOf(
				// Wocheneinkauf KW 23
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdWocheneinkauf, artikelId = artikelIdMilch, menge = 2.0, notiz = "Haltbare nehmen", erledigt = false),
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdWocheneinkauf, artikelId = artikelIdBrot, menge = 1.0, erledigt = false),
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdWocheneinkauf, artikelId = artikelIdEier, menge = 1.0, erledigt = true, notiz = "10er Pack war leer"), // 1x 6er Pack
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdWocheneinkauf, artikelId = artikelIdApfel, menge = 5.0, erledigt = false),
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdWocheneinkauf, artikelId = artikelIdKaese, menge = 1.0, erledigt = false),
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdWocheneinkauf, artikelId = artikelIdWaschmittel, menge = 1.0, erledigt = true),
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdWocheneinkauf, artikelId = artikelIdPaprika, menge = 1.0, erledigt = false), // 1x 500g Netz

				// Einkauf Wochenende
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdWochenende, artikelId = artikelIdBroetchen, menge = 1.0, notiz = "Frische vom Bäcker", erledigt = false), // 1x 6er Pack
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdWochenende, artikelId = artikelIdOrangensaft, menge = 1.0, erledigt = false),
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdWochenende, artikelId = artikelIdWurst, menge = 1.0, erledigt = false), // 1x 250g
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdWochenende, artikelId = artikelIdSchokolade, menge = 2.0, notiz = "Eine für mich!", erledigt = false),

				// Grillparty Vorbereitung
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdParty, artikelId = artikelIdHackfleisch, menge = 2000.0, notiz = "Für Burger & Frikadellen", erledigt = false), // 2kg
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdParty, artikelId = artikelIdHaehnchen, menge = 1200.0, notiz = "Für Spieße", erledigt = false), // 1.2kg
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdParty, artikelId = artikelIdWasser, menge = 24.0, erledigt = false), // 24 Flaschen (e.g. 2x 12er Kasten)
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdParty, artikelId = artikelIdCola, menge = 6.0, erledigt = false), // 6x 1L
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdParty, artikelId = artikelIdChips, menge = 5.0, erledigt = false),
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdParty, artikelId = artikelIdToast, menge = 2.0, notiz = "Für Kräuterbutter", erledigt = true), // 2x 500g

				// Schnell was holen (Erledigt)
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdSchnell, artikelId = artikelIdKlopapier, menge = 1.0, erledigt = true), // 1x 8 Rollen
				EinkaufsArtikelCrossRef(einkaufslisteId = listeIdSchnell, artikelId = artikelIdSpuelmittel, menge = 1.0, erledigt = true)
			)
			einkaufslisteDao.insertAllEinkaufsArtikel(listArtikelCrossRefs)
			Log.d("AppDatabaseCallback", "${listArtikelCrossRefs.size} list-article associations inserted.")

			val lagerbestand = listOf(

				LagerbestandEntity(
					artikelId = artikelIdMilch,
					menge = 2.0
				),

				LagerbestandEntity(
					artikelId = artikelIdKaese,
					menge = 500.0
				),

				LagerbestandEntity(
					artikelId = artikelIdEier,
					menge = 10.0
				),

				LagerbestandEntity(
					artikelId = artikelIdApfel,
					menge = 6.0
				),

				LagerbestandEntity(
					artikelId = artikelIdBrot,
					menge = 750.0
				),

				LagerbestandEntity(
					artikelId = artikelIdCola,
					menge = 3.0
				)

			)

			lagerbestand.forEach {

				lagerbestandDao.upsertLagerbestand(it)

			}

			Log.d(
				"AppDatabaseCallback",
				"${lagerbestand.size} Lagerbestände eingefügt."
			)

			val demoRezepte = listOf(

				RezeptEntity(
					id = rezeptIdSpaghetti,
					name = "Spaghetti Bolognese",
					beschreibung = "Klassischer italienischer Pasta-Klassiker.",
					zubereitungszeit = 35,
					portionen = 4,
					schwierigkeit = "Einfach",
					kategorie = "Hauptgericht",
					bildPfad = null
				),

				RezeptEntity(
					id = rezeptIdBurger,
					name = "Cheeseburger",
					beschreibung = "Saftiger Burger mit Käse.",
					zubereitungszeit = 25,
					portionen = 2,
					schwierigkeit = "Einfach",
					kategorie = "Fast Food",
					bildPfad = null
				),

				RezeptEntity(
					id = rezeptIdPancakes,
					name = "Pancakes",
					beschreibung = "Perfekt zum Frühstück.",
					zubereitungszeit = 20,
					portionen = 2,
					schwierigkeit = "Einfach",
					kategorie = "Frühstück",
					bildPfad = null
				),

				RezeptEntity(
					id = rezeptIdRuehrei,
					name = "Rührei",
					beschreibung = "Schnelles Frühstück.",
					zubereitungszeit = 10,
					portionen = 1,
					schwierigkeit = "Einfach",
					kategorie = "Frühstück",
					bildPfad = null
				),

				RezeptEntity(
					id = rezeptIdSalat,
					name = "Gemischter Salat",
					beschreibung = "Frischer Salat.",
					zubereitungszeit = 15,
					portionen = 2,
					schwierigkeit = "Einfach",
					kategorie = "Salat",
					bildPfad = null
				)

			)

			demoRezepte.forEach {

				rezeptDao.insertRezept(it)

			}

			val rezeptZutaten = listOf(

				// ---------- Spaghetti ----------

				RezeptZutatEntity(
					rezeptId = rezeptIdSpaghetti,
					artikelId = artikelIdHackfleisch,
					menge = 500.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdSpaghetti,
					artikelId = artikelIdTomate,
					menge = 400.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdSpaghetti,
					artikelId = artikelIdZwiebel,
					menge = 1.0
				),

				// ---------- Burger ----------

				RezeptZutatEntity(
					rezeptId = rezeptIdBurger,
					artikelId = artikelIdHackfleisch,
					menge = 300.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdBurger,
					artikelId = artikelIdKaese,
					menge = 150.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdBurger,
					artikelId = artikelIdBrot,
					menge = 2.0
				),

				// ---------- Pancakes ----------

				RezeptZutatEntity(
					rezeptId = rezeptIdPancakes,
					artikelId = artikelIdMilch,
					menge = 300.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdPancakes,
					artikelId = artikelIdEier,
					menge = 2.0
				),

				// ---------- Rührei ----------

				RezeptZutatEntity(
					rezeptId = rezeptIdRuehrei,
					artikelId = artikelIdEier,
					menge = 3.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdRuehrei,
					artikelId = artikelIdKaese,
					menge = 50.0
				),

				// ---------- Salat ----------

				RezeptZutatEntity(
					rezeptId = rezeptIdSalat,
					artikelId = artikelIdTomate,
					menge = 200.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdSalat,
					artikelId = artikelIdGurke,
					menge = 1.0
				)

			)

			rezeptDao.insertRezeptZutaten(
				rezeptZutaten
			)

			Log.d(
				"AppDatabaseCallback",
				"${rezeptZutaten.size} Rezeptzutaten eingefügt."
			)

			Log.i("AppDatabaseCallback", "DATABASE POPULATED SUCCESSFULLY WITH SAMPLE DATA.")
		}
	}
}

