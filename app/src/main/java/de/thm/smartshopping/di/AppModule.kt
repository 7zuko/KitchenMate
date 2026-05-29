package de.thm.smartshopping.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.thm.smartshopping.data.datastore.ShoppingModeRepository
import de.thm.smartshopping.data.db.AppDatabase
import de.thm.smartshopping.data.db.dao.ArtikelDao
import de.thm.smartshopping.data.db.dao.ArtikelKategorieDao
import de.thm.smartshopping.data.db.dao.EinkaufslisteDao
import de.thm.smartshopping.data.db.repository.ShoppingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

	@Provides
	@Singleton
	fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
		return Room.databaseBuilder(
			appContext,
			AppDatabase::class.java,
			"smart_shopping_db"
		)
			.addCallback(AppDatabase.AppDatabaseCallback(appContext))
			.fallbackToDestructiveMigration()
			.build()
	}

	@Provides
	fun provideEinkaufslisteDao(appDatabase: AppDatabase): EinkaufslisteDao {
		return appDatabase.einkaufslisteDao()
	}

	@Provides
	fun provideArtikelDao(appDatabase: AppDatabase): ArtikelDao {
		return appDatabase.artikelDao()
	}

	@Provides
	fun provideArtikelKategorieDao(appDatabase: AppDatabase): ArtikelKategorieDao {
		return appDatabase.artikelKategorieDao()
	}

	@Provides
	@Singleton
	fun provideShoppingRepository(
		einkaufslisteDao: EinkaufslisteDao,
		artikelDao: ArtikelDao,
		artikelKategorieDao: ArtikelKategorieDao
	): ShoppingRepository {
		return ShoppingRepository(einkaufslisteDao, artikelDao, artikelKategorieDao)
	}

	@Provides
	@Singleton
	fun provideShoppingModeRepository(
		@ApplicationContext appContext: Context
	): ShoppingModeRepository {
		return ShoppingModeRepository(appContext)
	}

}