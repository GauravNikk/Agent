
package com.gaurav.fieldagent.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(context: Context) {

    private val dataStore = context.dataStore

    private val offlineOnlyKey = booleanPreferencesKey("offline_only")
    private val autoRefreshKey = booleanPreferencesKey("auto_refresh")
    private val lastRefreshKey = longPreferencesKey("last_refresh")

    val isOfflineOnly: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[offlineOnlyKey] ?: false
        }

    suspend fun setOfflineOnly(isOfflineOnly: Boolean) {
        dataStore.edit { preferences ->
            preferences[offlineOnlyKey] = isOfflineOnly
        }
    }

    val isAutoRefresh: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[autoRefreshKey] ?: false
        }

    suspend fun setAutoRefresh(isAutoRefresh: Boolean) {
        dataStore.edit { preferences ->
            preferences[autoRefreshKey] = isAutoRefresh
        }
    }

    val lastRefresh: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[lastRefreshKey] ?: 0L
        }

    suspend fun setLastRefresh(lastRefresh: Long) {
        dataStore.edit { preferences ->
            preferences[lastRefreshKey] = lastRefresh
        }
    }
}
