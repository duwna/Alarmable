package com.duwna.alarmable.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.duwna.alarmable.data.api.City
import com.duwna.alarmable.utils.log
import kotlinx.coroutines.flow.first

class PrefManager(context: Context) {

    private val Context.dataStore by preferencesDataStore("app_preferences")
    private val dataStore = context.dataStore

    suspend fun saveLocation(city: City) {
        dataStore.edit { prefs ->
            prefs[Keys.ID_CITY_KEY] = city.id
            prefs[Keys.NAME_CITY_KEY] = city.name
        }
    }

    suspend fun getLocation(): City? {
        val prefs = dataStore.data.first()
        return City(
            prefs[Keys.ID_CITY_KEY] ?: return null,
            prefs[Keys.NAME_CITY_KEY] ?: return null
        )
    }

    private object Keys {
        val ID_CITY_KEY = intPreferencesKey("id")
        val NAME_CITY_KEY = stringPreferencesKey("name")
    }

}
