package com.hayakai.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(session: SessionModel) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = session.token
        }
    }


    fun getSession(): Flow<SessionModel> {
        return dataStore.data.map { preferences ->
            SessionModel(
                preferences[TOKEN_KEY] ?: "",
            )
        }
    }


    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveUser(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[EMAIL_KEY] = user.email
            preferences[PHONE_KEY] = user.phone ?: ""
            preferences[IMAGE_KEY] = user.image ?: ""
        }
    }

    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[NAME_KEY] ?: "",
                preferences[EMAIL_KEY] ?: "",
                preferences[PHONE_KEY] ?: "",
                preferences[IMAGE_KEY] ?: "",
            )
        }
    }

    suspend fun saveSettings(settings: SettingsModel) {

        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = settings.darkMode.toString()
            preferences[VOICE_DETECTION_KEY] = settings.voiceDetection.toString()
            preferences[VOICE_SENSITIVITY_KEY] = settings.voiceSensitivity
        }
    }

    fun getSettings(): Flow<SettingsModel> {
        return dataStore.data.map { preferences ->
            SettingsModel(
                preferences[DARK_MODE_KEY]?.toBoolean() ?: false,
                preferences[VOICE_DETECTION_KEY]?.toBoolean() ?: false,
                preferences[VOICE_SENSITIVITY_KEY] ?: "medium",
            )
        }
    }

    suspend fun saveSafetyScore(safetyScore: SafetyScore) {
        dataStore.edit { preferences ->
            preferences[SAFETY_SCORE_KEY] = safetyScore.score.toString()
            preferences[SAFETY_SCORE_TIMESTAMP_KEY] = safetyScore.timestamp.toString()
        }
    }

    fun getSafetyScore(): Flow<SafetyScore> {
        return dataStore.data.map { preferences ->
            SafetyScore(
                preferences[SAFETY_SCORE_KEY]?.toInt() ?: 0,
                preferences[SAFETY_SCORE_TIMESTAMP_KEY]?.toLong() ?: 0
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val TOKEN_KEY = stringPreferencesKey("token")

        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PHONE_KEY = stringPreferencesKey("phone")
        private val IMAGE_KEY = stringPreferencesKey("image")

        private val DARK_MODE_KEY = stringPreferencesKey("dark_mode")
        private val VOICE_DETECTION_KEY = stringPreferencesKey("voice_detection")
        private val VOICE_SENSITIVITY_KEY = stringPreferencesKey("voice_sensitivity")

        private val SAFETY_SCORE_KEY = stringPreferencesKey("safety_score")
        private val SAFETY_SCORE_TIMESTAMP_KEY = stringPreferencesKey("safety_score_timestamp")


        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}