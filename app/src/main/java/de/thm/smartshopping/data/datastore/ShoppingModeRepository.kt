package de.thm.smartshopping.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "shopping_settings")

class ShoppingModeRepository(private val context: Context) {

	companion object {
		val ACTIVE_SHOPPING_LIST_ID = stringPreferencesKey("active_shopping_list_id")
		val IS_SHOPPING_MODE_ACTIVE = booleanPreferencesKey("is_shopping_mode_active")
	}

	val activeShoppingListId: Flow<String?> = context.dataStore.data
		.map { preferences ->
			preferences[ACTIVE_SHOPPING_LIST_ID]
		}

	val isShoppingModeActive: Flow<Boolean> = context.dataStore.data
		.map { preferences ->
			preferences[IS_SHOPPING_MODE_ACTIVE] ?: false
		}

	suspend fun setShoppingMode(listId: String, isActive: Boolean) {
		context.dataStore.edit { settings ->
			if (isActive) {
				settings[ACTIVE_SHOPPING_LIST_ID] = listId
				settings[IS_SHOPPING_MODE_ACTIVE] = true
			} else {
				settings.remove(ACTIVE_SHOPPING_LIST_ID)
				settings[IS_SHOPPING_MODE_ACTIVE] = false
			}
		}
	}

	suspend fun clearShoppingMode() {
		context.dataStore.edit { settings ->
			settings.remove(ACTIVE_SHOPPING_LIST_ID)
			settings[IS_SHOPPING_MODE_ACTIVE] = false
		}
	}
}
