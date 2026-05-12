package com.dresta0056.whowantcoffee.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val VIEW_MODE = stringPreferencesKey("view_mode")
    }

    val sortOrder: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.SORT_ORDER] ?: "recent"
    }

    val viewMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.VIEW_MODE] ?: "list"
    }

    suspend fun setSortOrder(value: String) {
        dataStore.edit { preferences ->
            preferences[Keys.SORT_ORDER] = value
        }
    }

    suspend fun setViewMode(value: String) {
        dataStore.edit { preferences ->
            preferences[Keys.VIEW_MODE] = value
        }
    }
}