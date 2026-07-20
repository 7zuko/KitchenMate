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
import de.thm.smartshopping.data.db.dao.MealPlanDao
import de.thm.smartshopping.data.db.entity.ArtikelEntity
import de.thm.smartshopping.data.db.entity.ArtikelKategorieEntity
import de.thm.smartshopping.data.db.entity.EinkaufsArtikelCrossRef
import de.thm.smartshopping.data.db.entity.EinkaufslisteEntity
import de.thm.smartshopping.data.db.entity.RezeptZutatEntity
import de.thm.smartshopping.data.db.entity.LagerbestandEntity
import de.thm.smartshopping.data.db.entity.MealPlanEntity
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
		LagerbestandEntity::class,
		MealPlanEntity::class
	],
	version = 9,
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
	abstract fun mealPlanDao(): MealPlanDao
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
			val oneDay = 24 * 60 * 60 * 1000L
			val now = System.currentTimeMillis()

			val heute = now
			val morgen = now + oneDay
			val in3Tagen = now + (3 * oneDay)
			val in4Tagen = now + (4 * oneDay)
			val in5Tagen = now + (5 * oneDay)
			val in6Tagen = now + (6 * oneDay)
			val in7Tagen = now + (7 * oneDay)
			val in8Tagen = now + (8 * oneDay)
			val in12Tagen = now + (12 * oneDay)
			val in15Tagen = now + (15 * oneDay)
			val in20Tagen = now + (20 * oneDay)
			val in25Tagen = now + (25 * oneDay)
			val in30Tagen = now + (30 * oneDay)
			val in8Monaten = now + (240 * oneDay)
			val in10Monaten = now + (300 * oneDay)
			val in18Monaten = now + (540 * oneDay)
			val in1Jahr = now + (365 * oneDay)

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

			val artikelIdSpaghetti = UUID.randomUUID().toString()
			val artikelIdPassierteTomaten = UUID.randomUUID().toString()
			val artikelIdOlivenoel = UUID.randomUUID().toString()
			val artikelIdButter = UUID.randomUUID().toString()
			val artikelIdParmesan = UUID.randomUUID().toString()
			val artikelIdMozzarella = UUID.randomUUID().toString()
			val artikelIdMehl = UUID.randomUUID().toString()
			val artikelIdKidneybohnen = UUID.randomUUID().toString()
			val artikelIdSalat = UUID.randomUUID().toString()
			val artikelIdKarotten = UUID.randomUUID().toString()
			val artikelIdBrokkoli = UUID.randomUUID().toString()
			val artikelIdKnoblauch = UUID.randomUUID().toString()
			val artikelIdErdbeeren = UUID.randomUUID().toString()
			val artikelIdZitronen = UUID.randomUUID().toString()
			val artikelIdMuellbeutel = UUID.randomUUID().toString()


			val articles = listOf(
				ArtikelEntity(id = artikelIdApfel, name = "Äpfel", einheit = "Stk", kategorieId = katIdObst, emoji = "\uD83C\uDF4E"),
				ArtikelEntity(id = artikelIdBanane, name = "Bananen", einheit = "Stk", kategorieId = katIdObst, emoji = "\uD83C\uDF4C"),
				ArtikelEntity(id = artikelIdOrangen, name = "Orangen", einheit = "Stk", kategorieId= katIdObst, emoji = "\uD83C\uDF4A"),
				ArtikelEntity(
					id = artikelIdErdbeeren,
					name = "Erdbeeren",
					einheit = "Gramm",
					kategorieId = katIdObst,
					emoji = "🍓"
				),

				ArtikelEntity(
					id = artikelIdZitronen,
					name = "Zitronen",
					einheit = "Stk",
					kategorieId = katIdObst,
					emoji = "🍋"
				),

				ArtikelEntity(id = artikelIdTomate, name = "Tomaten", einheit = "Gramm", kategorieId = katIdGemuese, emoji = "\uD83C\uDF45"),
				ArtikelEntity(id = artikelIdGurke, name = "Gurken", einheit = "Stk", kategorieId = katIdGemuese, emoji = "\uD83E\uDD52"),
				ArtikelEntity(id = artikelIdPaprika, name = "Paprika", einheit = "Gramm", kategorieId = katIdGemuese, emoji = "\uD83E\uDED1"),
				ArtikelEntity(id = artikelIdZwiebel, name = "Zwiebeln", einheit = "Gramm", kategorieId = katIdGemuese, emoji = "\uD83E\uDDC5"),
				ArtikelEntity(
					id = artikelIdPassierteTomaten,
					name = "Passierte Tomaten",
					einheit = "Gramm",
					kategorieId = katIdGemuese,
					emoji = "🥫"
				),

				ArtikelEntity(
					id = artikelIdKarotten,
					name = "Karotten",
					einheit = "Gramm",
					kategorieId = katIdGemuese,
					emoji = "🥕"
				),

				ArtikelEntity(
					id = artikelIdBrokkoli,
					name = "Brokkoli",
					einheit = "Gramm",
					kategorieId = katIdGemuese,
					emoji = "🥦"
				),

				ArtikelEntity(
					id = artikelIdSalat,
					name = "Salat",
					einheit = "Stk",
					kategorieId = katIdGemuese,
					emoji = "🥬"
				),

				ArtikelEntity(
					id = artikelIdKnoblauch,
					name = "Knoblauch",
					einheit = "Zehen",
					kategorieId = katIdGemuese,
					emoji = "🧄"
				),

				ArtikelEntity(id = artikelIdMilch, name = "Vollmilch (3.5%)", einheit = "Liter", kategorieId = katIdMilchprodukte, emoji = "\uD83E\uDD5B"),
				ArtikelEntity(id = artikelIdJoghurt, name = "Joghurt (1.5%)", einheit = "Gramm", kategorieId = katIdMilchprodukte, emoji = "\uD83E\uDD5B"),
				ArtikelEntity(id = artikelIdEier, name = "Eier (Größe S, Bio)", einheit = "Stk", kategorieId = katIdMilchprodukte, emoji = "\uD83E\uDD5A"),
				ArtikelEntity(id = artikelIdKaese, name = "Gouda", einheit = "Gramm", kategorieId = katIdMilchprodukte, emoji = "\uD83E\uDDC0"),
				ArtikelEntity(
					id = artikelIdButter,
					name = "Butter",
					einheit = "Gramm",
					kategorieId = katIdMilchprodukte,
					emoji = "🧈"
				),

				ArtikelEntity(
					id = artikelIdParmesan,
					name = "Parmesan",
					einheit = "Gramm",
					kategorieId = katIdMilchprodukte,
					emoji = "🧀"
				),

				ArtikelEntity(
					id = artikelIdMozzarella,
					name = "Mozzarella",
					einheit = "Stk",
					kategorieId = katIdMilchprodukte,
					emoji = "🧀"
				),


				ArtikelEntity(id = artikelIdBrot, name = "Brot", einheit = "Gramm", kategorieId = katIdBackwaren, emoji = "\uD83E\uDD56"),
				ArtikelEntity(id = artikelIdBroetchen, name = "Aufbackbrötchen", einheit = "Stk", kategorieId = katIdBackwaren, emoji = "\uD83E\uDD56"),
				ArtikelEntity(id = artikelIdToast, name = "Vollkorntoast", einheit = "Gramm", kategorieId = katIdBackwaren, emoji = "\uD83C\uDF5E"),
				ArtikelEntity(
					id = artikelIdSpaghetti,
					name = "Spaghetti",
					einheit = "Gramm",
					kategorieId = katIdBackwaren,
					emoji = "🍝"
				),

				ArtikelEntity(
					id = artikelIdMehl,
					name = "Mehl",
					einheit = "Gramm",
					kategorieId = katIdBackwaren,
					emoji = "🌾"
				),

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
				ArtikelEntity(
					id = artikelIdMuellbeutel,
					name = "Müllbeutel",
					einheit = "Stk",
					kategorieId = katIdHaushalt,
					emoji = "🗑️"
				),

				ArtikelEntity(id = artikelIdMais, name = "Mais", einheit = "Gramm", kategorieId = katIdKonserven, emoji = "\uD83E\uDD6B"),
				ArtikelEntity(id = artikelIdThunfisch, name = "Thunfisch", einheit = "Gramm", kategorieId = katIdKonserven, emoji = "\uD83E\uDD6B"),
				ArtikelEntity(
					id = artikelIdKidneybohnen,
					name = "Kidneybohnen",
					einheit = "Gramm",
					kategorieId = katIdKonserven,
					emoji = "🫘"
				),
				ArtikelEntity(
					id = artikelIdOlivenoel,
					name = "Olivenöl",
					einheit = "ml",
					kategorieId = katIdHaushalt,
					emoji = "🫒"
				),

				ArtikelEntity(id = artikelIdKatzenfutter, name = "Katzenfutter", einheit = "Gramm", kategorieId = katIdTierbedarf, emoji = "\uD83D\uDE3C")
			)
			artikelDao.insertAllArtikel(articles)
			Log.d("AppDatabaseCallback", "${articles.size} articles inserted.")


			// --- Sample Rezepte ---
			val rezeptIdSpaghetti = UUID.randomUUID().toString()
			val rezeptIdChili = UUID.randomUUID().toString()
			val rezeptIdCaprese = UUID.randomUUID().toString()
			val rezeptIdPancakes = UUID.randomUUID().toString()
			val rezeptIdRuehrei = UUID.randomUUID().toString()
			val rezeptIdCaesar = UUID.randomUUID().toString()
			val rezeptIdCurry = UUID.randomUUID().toString()
			val rezeptIdKuchen = UUID.randomUUID().toString()




			// --- Sample Shopping Lists ---
			val currentTimeMillis = System.currentTimeMillis()
			val userDemoId = "demoUser123" // Example User ID

			val listeIdMonat = UUID.randomUUID().toString()
			val listeIdGrill = UUID.randomUUID().toString()
			val listeIdTier = UUID.randomUUID().toString()
			val listeIdFruehstueck = UUID.randomUUID().toString()

			// Inserting EinkaufslisteEntity one by one as per your DAO
			val einkaufslisteMonat = EinkaufslisteEntity(
				id = listeIdMonat,
				name = "Monatseinkauf",
				erstellDatumMillis = currentTimeMillis,
				bearbeitetAmMillis = currentTimeMillis,
				erstellerId = userDemoId
			)

			val einkaufslisteGrill = EinkaufslisteEntity(
				id = listeIdGrill,
				name = "Grillabend",
				erstellDatumMillis = currentTimeMillis,
				bearbeitetAmMillis = currentTimeMillis,
				erstellerId = userDemoId
			)

			val einkaufslisteTier = EinkaufslisteEntity(
				id = listeIdTier,
				name = "Tierbedarf",
				erstellDatumMillis = currentTimeMillis,
				bearbeitetAmMillis = currentTimeMillis,
				erstellerId = userDemoId
			)

			val einkaufslisteFruehstueck = EinkaufslisteEntity(
				id = listeIdFruehstueck,
				name = "Frühstück",
				erstellDatumMillis = currentTimeMillis,
				bearbeitetAmMillis = currentTimeMillis,
				erstellerId = userDemoId
			)

			einkaufslisteDao.insertEinkaufsliste(einkaufslisteMonat)
			einkaufslisteDao.insertEinkaufsliste(einkaufslisteGrill)
			einkaufslisteDao.insertEinkaufsliste(einkaufslisteTier)
			einkaufslisteDao.insertEinkaufsliste(einkaufslisteFruehstueck)
			Log.d("AppDatabaseCallback", "4 shopping lists inserted individually.")

			// --- Add Articles to Shopping Lists (Many-to-Many using CrossRef) ---
			val listArtikelCrossRefs = listOf(
				// Wocheneinkauf KW 23
				EinkaufsArtikelCrossRef(
					einkaufslisteId = listeIdMonat,
					artikelId = artikelIdMilch,
					menge = 2.0,
					erledigt = false
				),

				EinkaufsArtikelCrossRef(
					einkaufslisteId = listeIdMonat,
					artikelId = artikelIdButter,
					menge = 1.0,
					erledigt = false
				),

				EinkaufsArtikelCrossRef(
					einkaufslisteId = listeIdMonat,
					artikelId = artikelIdBrot,
					menge = 1.0,
					erledigt = true
				),

				EinkaufsArtikelCrossRef(
					einkaufslisteId = listeIdGrill,
					artikelId = artikelIdHackfleisch,
					menge = 2000.0,
					erledigt = false
				),

				EinkaufsArtikelCrossRef(
					einkaufslisteId = listeIdGrill,
					artikelId = artikelIdHaehnchen,
					menge = 1000.0,
					erledigt = false
				),

				EinkaufsArtikelCrossRef(
					einkaufslisteId = listeIdGrill,
					artikelId = artikelIdCola,
					menge = 6.0,
					erledigt = false
				),

				EinkaufsArtikelCrossRef(
					einkaufslisteId = listeIdGrill,
					artikelId = artikelIdChips,
					menge = 4.0,
					erledigt = false
				),

				EinkaufsArtikelCrossRef(
					einkaufslisteId = listeIdTier,
					artikelId = artikelIdKatzenfutter,
					menge = 1500.0,
					erledigt = false
				),

				EinkaufsArtikelCrossRef(
					einkaufslisteId = listeIdFruehstueck,
					artikelId = artikelIdBroetchen,
					menge = 6.0,
					erledigt = false
				),

				EinkaufsArtikelCrossRef(
					einkaufslisteId = listeIdFruehstueck,
					artikelId = artikelIdOrangensaft,
					menge = 1.0,
					erledigt = false
				),

				EinkaufsArtikelCrossRef(
					einkaufslisteId = listeIdFruehstueck,
					artikelId = artikelIdEier,
					menge = 10.0,
					erledigt = false
				),
			)
			einkaufslisteDao.insertAllEinkaufsArtikel(listArtikelCrossRefs)
			Log.d("AppDatabaseCallback", "${listArtikelCrossRefs.size} list-article associations inserted.")

			val lagerbestand = listOf(

				// ---------- Gemüse ----------

				LagerbestandEntity(
					artikelId = artikelIdPassierteTomaten,
					menge = 2.0,
					mindesthaltbarBis = morgen
				),

				LagerbestandEntity(
					artikelId = artikelIdTomate,
					menge = 600.0,
					mindesthaltbarBis = in4Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdZwiebel,
					menge = 4.0,
					mindesthaltbarBis = in25Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdKnoblauch,
					menge = 5.0,
					mindesthaltbarBis = in20Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdPaprika,
					menge = 400.0,
					mindesthaltbarBis = in6Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdGurke,
					menge = 2.0,
					mindesthaltbarBis = in6Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdKarotten,
					menge = 800.0,
					mindesthaltbarBis = in8Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdBrokkoli,
					menge = 500.0,
					mindesthaltbarBis = in5Tagen
				),

				// ---------- Milchprodukte ----------

				LagerbestandEntity(
					artikelId = artikelIdMilch,
					menge = 2.0,
					mindesthaltbarBis = in5Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdButter,
					menge = 250.0,
					mindesthaltbarBis = in12Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdEier,
					menge = 10.0,
					mindesthaltbarBis = in7Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdMozzarella,
					menge = 2.0,
					mindesthaltbarBis = heute
				),

				LagerbestandEntity(
					artikelId = artikelIdParmesan,
					menge = 250.0,
					mindesthaltbarBis = in30Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdKaese,
					menge = 500.0,
					mindesthaltbarBis = in15Tagen
				),

				// ---------- Backwaren ----------

				LagerbestandEntity(
					artikelId = artikelIdSpaghetti,
					menge = 500.0,
					mindesthaltbarBis = in1Jahr
				),

				LagerbestandEntity(
					artikelId = artikelIdBrot,
					menge = 700.0,
					mindesthaltbarBis = in5Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdToast,
					menge = 500.0,
					mindesthaltbarBis = in8Tagen
				),

				LagerbestandEntity(
					artikelId = artikelIdMehl,
					menge = 1000.0,
					mindesthaltbarBis = in1Jahr
				),

				// ---------- Fleisch ----------

				LagerbestandEntity(
					artikelId = artikelIdHaehnchen,
					menge = 600.0,
					mindesthaltbarBis = in3Tagen
				),

				// Kein Hackfleisch!

				// ---------- Konserven ----------

				LagerbestandEntity(
					artikelId = artikelIdMais,
					menge = 300.0,
					mindesthaltbarBis = in1Jahr
				),

				LagerbestandEntity(
					artikelId = artikelIdKidneybohnen,
					menge = 500.0,
					mindesthaltbarBis = in1Jahr
				),

				LagerbestandEntity(
					artikelId = artikelIdThunfisch,
					menge = 300.0,
					mindesthaltbarBis = in1Jahr
				),

				// ---------- Getränke ----------

				LagerbestandEntity(
					artikelId = artikelIdWasser,
					menge = 12.0,
					mindesthaltbarBis = null
				),

				LagerbestandEntity(
					artikelId = artikelIdCola,
					menge = 3.0,
					mindesthaltbarBis = in8Monaten
				),

				LagerbestandEntity(
					artikelId = artikelIdKaffee,
					menge = 1000.0,
					mindesthaltbarBis = in10Monaten
				),

				// ---------- Sonstiges ----------

				LagerbestandEntity(
					artikelId = artikelIdOlivenoel,
					menge = 750.0,
					mindesthaltbarBis = in18Monaten
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
					bildPfad = "spaghetti.jpg"
				),

				RezeptEntity(
					id = rezeptIdChili,
					name = "Chili con Carne",
					beschreibung = "Würziges Chili mit Mais und Kidneybohnen.",
					zubereitungszeit = 40,
					portionen = 4,
					schwierigkeit = "Einfach",
					kategorie = "Hauptgericht",
					bildPfad = "chili.jpg"
				),

				RezeptEntity(
					id = rezeptIdCaprese,
					name = "Caprese",
					beschreibung = "Tomate mit Mozzarella und Olivenöl.",
					zubereitungszeit = 10,
					portionen = 2,
					schwierigkeit = "Einfach",
					kategorie = "Salat",
					bildPfad = "caprese.jpg"
				),

				RezeptEntity(
					id = rezeptIdPancakes,
					name = "Pancakes",
					beschreibung = "Locker und fluffig.",
					zubereitungszeit = 20,
					portionen = 2,
					schwierigkeit = "Einfach",
					kategorie = "Frühstück",
					bildPfad = "pancakes.jpg"
				),

				RezeptEntity(
					id = rezeptIdRuehrei,
					name = "Rührei",
					beschreibung = "Perfekt zum Frühstück.",
					zubereitungszeit = 10,
					portionen = 1,
					schwierigkeit = "Einfach",
					kategorie = "Frühstück",
					bildPfad = "ruehrei.jpg"
				),

				RezeptEntity(
					id = rezeptIdCaesar,
					name = "Caesar Salad",
					beschreibung = "Frischer Salat mit Hähnchen.",
					zubereitungszeit = 20,
					portionen = 2,
					schwierigkeit = "Einfach",
					kategorie = "Salat",
					bildPfad = "caesar.jpg"
				),

				RezeptEntity(
					id = rezeptIdCurry,
					name = "Hähnchen Curry",
					beschreibung = "Leckeres Curry mit Gemüse.",
					zubereitungszeit = 35,
					portionen = 4,
					schwierigkeit = "Mittel",
					kategorie = "Hauptgericht",
					bildPfad = "curry.jpg"
				),

				RezeptEntity(
					id = rezeptIdKuchen,
					name = "Schokokuchen",
					beschreibung = "Saftiger Schokokuchen.",
					zubereitungszeit = 60,
					portionen = 8,
					schwierigkeit = "Einfach",
					kategorie = "Dessert",
					bildPfad = "schokokuchen.jpg"
				)
			)

			demoRezepte.forEach {

				rezeptDao.insertRezept(it)

			}

			val rezeptZutaten = listOf(

				// ---------- Spaghetti ----------

				RezeptZutatEntity(
					rezeptId = rezeptIdSpaghetti,
					artikelId = artikelIdSpaghetti,
					menge = 500.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdSpaghetti,
					artikelId = artikelIdHackfleisch,
					menge = 500.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdSpaghetti,
					artikelId = artikelIdPassierteTomaten,
					menge = 400.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdSpaghetti,
					artikelId = artikelIdZwiebel,
					menge = 1.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdSpaghetti,
					artikelId = artikelIdOlivenoel,
					menge = 20.0
				),

				// ---------- Chili ----------

				RezeptZutatEntity(
					rezeptId = rezeptIdChili,
					artikelId = artikelIdHackfleisch,
					menge = 300.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdChili,
					artikelId = artikelIdMais,
					menge = 200.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdChili,
					artikelId = artikelIdKidneybohnen,
					menge = 250.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdChili,
					artikelId = artikelIdZwiebel,
					menge = 1.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdChili,
					artikelId = artikelIdPassierteTomaten,
					menge = 400.0
				),

				// ---------- Caprese ----------

				RezeptZutatEntity(
					rezeptId = rezeptIdCaprese,
					artikelId = artikelIdTomate,
					menge = 300.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdCaprese,
					artikelId = artikelIdMozzarella,
					menge = 2.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdCaprese,
					artikelId = artikelIdOlivenoel,
					menge = 20.0
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

				RezeptZutatEntity(
					rezeptId = rezeptIdPancakes,
					artikelId = artikelIdMehl,
					menge = 250.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdPancakes,
					artikelId = artikelIdButter,
					menge = 30.0
				),

				// ---------- Rührei ----------

				RezeptZutatEntity(
					rezeptId = rezeptIdRuehrei,
					artikelId = artikelIdEier,
					menge = 3.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdRuehrei,
					artikelId = artikelIdButter,
					menge = 20.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdCaesar,
					artikelId = artikelIdSalat,
					menge = 1.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdCaesar,
					artikelId = artikelIdHaehnchen,
					menge = 250.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdCaesar,
					artikelId = artikelIdParmesan,
					menge = 40.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdCurry,
					artikelId = artikelIdHaehnchen,
					menge = 400.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdCurry,
					artikelId = artikelIdBrokkoli,
					menge = 300.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdCurry,
					artikelId = artikelIdKarotten,
					menge = 200.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdCurry,
					artikelId = artikelIdZwiebel,
					menge = 1.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdKuchen,
					artikelId = artikelIdMehl,
					menge = 300.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdKuchen,
					artikelId = artikelIdButter,
					menge = 200.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdKuchen,
					artikelId = artikelIdEier,
					menge = 4.0
				),

				RezeptZutatEntity(
					rezeptId = rezeptIdKuchen,
					artikelId = artikelIdSchokolade,
					menge = 200.0
				),

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

