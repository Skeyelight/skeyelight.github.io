package com.example.dailyweighttracker_kotlin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

// Handles user preferences
class UserPreferencesDAO(private val context: Context) {

    // Creates key by userId
    private fun getPreferenceKeys(userId: Int) = object {
        val WEIGHT_UNIT = stringPreferencesKey("weight_unit_$userId")
        val GOAL_NOTIFICATION = booleanPreferencesKey("goal_notification_$userId")
    }

    // Gets the weight unit preference
    fun getUnitPreference(userId: Int): Flow<WeightUnit> {
        val preferenceKeys = getPreferenceKeys(userId)
        return context.dataStore.data.map { preferences ->
            val unitName = preferences[preferenceKeys.WEIGHT_UNIT] ?: WeightUnit.LBS.name
            WeightUnit.valueOf(unitName)
        }
    }

    // Updates weight unit preference
    suspend fun updateUnitPreference(userId: Int, weightUnit: WeightUnit) {
        val preferenceKeys = getPreferenceKeys(userId)
        try {
            context.dataStore.edit { preferences ->
                preferences[preferenceKeys.WEIGHT_UNIT] = weightUnit.name
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Gets goal notification preference
    fun getGoalNotification(userId: Int): Flow<Boolean> {
        val preferenceKeys = getPreferenceKeys(userId)
        return context.dataStore.data.map { preferences ->
            preferences[preferenceKeys.GOAL_NOTIFICATION] ?: false
        }
    }

    // Updates goal notification preference
    suspend fun updateGoalNotification(userId: Int, enabled: Boolean) {
        val preferenceKeys = getPreferenceKeys(userId)
        try {
            context.dataStore.edit { preferences ->
                preferences[preferenceKeys.GOAL_NOTIFICATION] = enabled
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
