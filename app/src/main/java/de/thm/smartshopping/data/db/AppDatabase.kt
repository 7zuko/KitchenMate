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
import de.thm.smartshopping.data.db.entity.ArtikelEntity
import de.thm.smartshopping.data.db.entity.ArtikelKategorieEntity
import de.thm.smartshopping.data.db.entity.EinkaufsArtikelCrossRef
import de.thm.smartshopping.data.db.entity.EinkaufslisteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
	entities = [
		EinkaufslisteEntity::class,
		ArtikelEntity::class,
		ArtikelKategorieEntity::class,
		EinkaufsArtikelCrossRef::class
	],
	version = 1,
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

			Log.d("AppDatabaseCallback", "Starting data population within populateDatabase...")

			// --- Sample Categories ---
			val katIdObstGemuese = UUID.randomUUID().toString()
			val katIdMilchprodukte = UUID.randomUUID().toString()
			val katIdBackwaren = UUID.randomUUID().toString()
			val katIdFleisch = UUID.randomUUID().toString()
			val katIdGetraenke = UUID.randomUUID().toString()
			val katIdSuesswaren = UUID.randomUUID().toString()
			val katIdHaushalt = UUID.randomUUID().toString()
			val katIdKonserven = UUID.randomUUID().toString()
			val katIdTierbedarf = UUID.randomUUID().toString()


			val categories = listOf(
				ArtikelKategorieEntity(id = katIdObstGemuese, name = "Obst & Gemüse"),
				ArtikelKategorieEntity(id = katIdMilchprodukte, name = "Milchprodukte & Eier"),
				ArtikelKategorieEntity(id = katIdBackwaren, name = "Brot & Backwaren"),
				ArtikelKategorieEntity(id = katIdFleisch, name = "Fleisch & Wurst"),
				ArtikelKategorieEntity(id = katIdGetraenke, name = "Getränke"),
				ArtikelKategorieEntity(id = katIdSuesswaren, name = "Süßwaren & Snacks"),
				ArtikelKategorieEntity(id = katIdHaushalt, name = "Haushalt & Reinigung"),
				ArtikelKategorieEntity(id = katIdKonserven, name = "Konserven & Fertiggerichte"),
				ArtikelKategorieEntity(id = katIdTierbedarf, name = "Tierbedarf")
			)
			artikelKategorieDao.insertAllArtikelKategorien(categories)
			Log.d("AppDatabaseCallback", "${categories.size} categories inserted.")

			// --- Sample Articles ---
			// Obst & Gemüse
			val artikelIdApfel = UUID.randomUUID().toString()
			val artikelIdBanane = UUID.randomUUID().toString()
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
				ArtikelEntity(id = artikelIdApfel, name = "Äpfel (Braeburn)", einheit = "Stk", kategorieId = katIdObstGemuese),
				ArtikelEntity(id = artikelIdBanane, name = "Bananen (Fairtrade)", einheit = "Stk", kategorieId = katIdObstGemuese),
				ArtikelEntity(id = artikelIdTomate, name = "Tomaten (Cherry)", einheit = "250g Schale", kategorieId = katIdObstGemuese),
				ArtikelEntity(id = artikelIdGurke, name = "Salatgurke", einheit = "Stk", kategorieId = katIdObstGemuese),
				ArtikelEntity(id = artikelIdPaprika, name = "Paprika-Mix (rot, gelb, grün)", einheit = "500g Netz", kategorieId = katIdObstGemuese),
				ArtikelEntity(id = artikelIdZwiebel, name = "Zwiebeln (gelb)", einheit = "1kg Netz", kategorieId = katIdObstGemuese),

				ArtikelEntity(id = artikelIdMilch, name = "Frische Vollmilch (3.5%)", einheit = "1 Liter", kategorieId = katIdMilchprodukte),
				ArtikelEntity(id = artikelIdJoghurt, name = "Naturjoghurt (1.5%)", einheit = "500g Becher", kategorieId = katIdMilchprodukte),
				ArtikelEntity(id = artikelIdEier, name = "Eier (Größe L, Bio)", einheit = "6er Pack", kategorieId = katIdMilchprodukte),
				ArtikelEntity(id = artikelIdKaese, name = "Gouda (in Scheiben)", einheit = "200g Packung", kategorieId = katIdMilchprodukte),

				ArtikelEntity(id = artikelIdBrot, name = "Roggenmischbrot", einheit = "750g Laib", kategorieId = katIdBackwaren),
				ArtikelEntity(id = artikelIdBroetchen, name = "Mehrkornbrötchen (zum Aufbacken)", einheit = "6er Pack", kategorieId = katIdBackwaren),
				ArtikelEntity(id = artikelIdToast, name = "Vollkorntoast", einheit = "500g Packung", kategorieId = katIdBackwaren),

				ArtikelEntity(id = artikelIdHackfleisch, name = "Gemischtes Hackfleisch", einheit = "500g", kategorieId = katIdFleisch),
				ArtikelEntity(id = artikelIdHaehnchen, name = "Hähnchenbrustfilet (Bio)", einheit = "400g", kategorieId = katIdFleisch),
				ArtikelEntity(id = artikelIdWurst, name = "Salami (am Stück)", einheit = "250g", kategorieId = katIdFleisch),

				ArtikelEntity(id = artikelIdWasser, name = "Mineralwasser (Still)", einheit = "1.5L Flasche", kategorieId = katIdGetraenke),
				ArtikelEntity(id = artikelIdCola, name = "Cola (Zero)", einheit = "1L Flasche", kategorieId = katIdGetraenke),
				ArtikelEntity(id = artikelIdOrangensaft, name = "Orangensaft (100% Frucht)", einheit = "1 Liter", kategorieId = katIdGetraenke),
				ArtikelEntity(id = artikelIdKaffee, name = "Kaffeebohnen (Crema)", einheit = "1kg Beutel", kategorieId = katIdGetraenke),

				ArtikelEntity(id = artikelIdSchokolade, name = "Edelbitterschokolade (70%)", einheit = "100g Tafel", kategorieId = katIdSuesswaren),
				ArtikelEntity(id = artikelIdChips, name = "Kartoffelchips (Meersalz)", einheit = "175g Beutel", kategorieId = katIdSuesswaren),
				ArtikelEntity(id = artikelIdGummibaerchen, name = "Gummibärchen", einheit = "200g Beutel", kategorieId = katIdSuesswaren),

				ArtikelEntity(id = artikelIdWaschmittel, name = "Waschmittel (Universal)", einheit = "20 WL", kategorieId = katIdHaushalt),
				ArtikelEntity(id = artikelIdKlopapier, name = "Toilettenpapier (Recycling, 4-lagig)", einheit = "8 Rollen", kategorieId = katIdHaushalt),
				ArtikelEntity(id = artikelIdSpuelmittel, name = "Spülmittel (Sensitiv)", einheit = "500ml Flasche", kategorieId = katIdHaushalt),

				ArtikelEntity(id = artikelIdMais, name = "Mais (Dose)", einheit = "400g Dose", kategorieId = katIdKonserven),
				ArtikelEntity(id = artikelIdThunfisch, name = "Thunfisch (in eigenem Saft)", einheit = "150g Dose", kategorieId = katIdKonserven),

				ArtikelEntity(id = artikelIdKatzenfutter, name = "Katzenfutter (Nass, Huhn)", einheit = "85g Beutel", kategorieId = katIdTierbedarf)
			)
			artikelDao.insertAllArtikel(articles)
			Log.d("AppDatabaseCallback", "${articles.size} articles inserted.")

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

			Log.i("AppDatabaseCallback", "DATABASE POPULATED SUCCESSFULLY WITH SAMPLE DATA.")
		}
	}
}

