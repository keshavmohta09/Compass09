package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.models.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "compass_settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val SHOW_LEVEL = booleanPreferencesKey("show_level")
        val SHOW_VASTU = booleanPreferencesKey("show_vastu")
        val DECLINATION = floatPreferencesKey("declination")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            keepScreenOn = preferences[PreferencesKeys.KEEP_SCREEN_ON] ?: true,
            hapticFeedbackEnabled = preferences[PreferencesKeys.HAPTICS_ENABLED] ?: true,
            showLevelBubble = preferences[PreferencesKeys.SHOW_LEVEL] ?: true,
            showVastuCard = preferences[PreferencesKeys.SHOW_VASTU] ?: true,
            magneticDeclination = preferences[PreferencesKeys.DECLINATION] ?: 0f
        )
    }

    suspend fun setKeepScreenOn(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEEP_SCREEN_ON] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setShowLevel(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_LEVEL] = enabled }
    }

    suspend fun setShowVastu(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_VASTU] = enabled }
    }

    suspend fun setDeclination(degrees: Float) {
        context.dataStore.edit { it[PreferencesKeys.DECLINATION] = degrees }
    }
}
