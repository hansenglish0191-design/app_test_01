package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dailyHealthDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "daily_health_prefs")

class DailyHealthRepository(
  private val context: Context,
  private val dateProvider: () -> String = { currentLocalDateKey() },
) {
  fun todayDateKey(): String = dateProvider()

  private object Keys {
    val SAVED_DATE = stringPreferencesKey("saved_date")
    val MORNING_TAKEN = booleanPreferencesKey("morning_taken")
    val LUNCH_TAKEN = booleanPreferencesKey("lunch_taken")
    val DINNER_TAKEN = booleanPreferencesKey("dinner_taken")
    val WATER_CUPS = intPreferencesKey("water_cups")
  }

  val dailyState: Flow<DailyHealthState> =
    context.dailyHealthDataStore.data.map { prefs ->
      val today = dateProvider()
      val savedDate = prefs[Keys.SAVED_DATE]
      if (savedDate == today) {
        DailyHealthState(
          dateKey = today,
          morningTaken = prefs[Keys.MORNING_TAKEN] ?: false,
          lunchTaken = prefs[Keys.LUNCH_TAKEN] ?: false,
          dinnerTaken = prefs[Keys.DINNER_TAKEN] ?: false,
          waterCups = (prefs[Keys.WATER_CUPS] ?: 0).coerceAtLeast(0),
        )
      } else {
        DailyHealthState(dateKey = today)
      }
    }

  suspend fun resetIfDateChanged() {
    val today = dateProvider()
    context.dailyHealthDataStore.edit { prefs ->
      if (prefs[Keys.SAVED_DATE] != today) {
        prefs[Keys.SAVED_DATE] = today
        prefs[Keys.MORNING_TAKEN] = false
        prefs[Keys.LUNCH_TAKEN] = false
        prefs[Keys.DINNER_TAKEN] = false
        prefs[Keys.WATER_CUPS] = 0
      }
    }
  }

  suspend fun toggleMedication(slot: MedicationSlot) {
    val today = dateProvider()
    context.dailyHealthDataStore.edit { prefs ->
      val isSameDay = prefs[Keys.SAVED_DATE] == today
      if (!isSameDay) {
        prefs[Keys.SAVED_DATE] = today
        prefs[Keys.MORNING_TAKEN] = false
        prefs[Keys.LUNCH_TAKEN] = false
        prefs[Keys.DINNER_TAKEN] = false
        prefs[Keys.WATER_CUPS] = 0
      }
      val key =
        when (slot) {
          MedicationSlot.MORNING -> Keys.MORNING_TAKEN
          MedicationSlot.LUNCH -> Keys.LUNCH_TAKEN
          MedicationSlot.DINNER -> Keys.DINNER_TAKEN
        }
      val current = prefs[key] ?: false
      prefs[key] = !current
    }
  }

  suspend fun addWaterCup() {
    val today = dateProvider()
    context.dailyHealthDataStore.edit { prefs ->
      val isSameDay = prefs[Keys.SAVED_DATE] == today
      if (!isSameDay) {
        prefs[Keys.SAVED_DATE] = today
        prefs[Keys.MORNING_TAKEN] = false
        prefs[Keys.LUNCH_TAKEN] = false
        prefs[Keys.DINNER_TAKEN] = false
        prefs[Keys.WATER_CUPS] = 1
      } else {
        val current = (prefs[Keys.WATER_CUPS] ?: 0).coerceAtLeast(0)
        prefs[Keys.WATER_CUPS] = current + 1
      }
    }
  }

  suspend fun removeWaterCup() {
    val today = dateProvider()
    context.dailyHealthDataStore.edit { prefs ->
      val isSameDay = prefs[Keys.SAVED_DATE] == today
      if (!isSameDay) {
        prefs[Keys.SAVED_DATE] = today
        prefs[Keys.MORNING_TAKEN] = false
        prefs[Keys.LUNCH_TAKEN] = false
        prefs[Keys.DINNER_TAKEN] = false
        prefs[Keys.WATER_CUPS] = 0
      } else {
        val current = prefs[Keys.WATER_CUPS] ?: 0
        prefs[Keys.WATER_CUPS] = (current - 1).coerceAtLeast(0)
      }
    }
  }

  companion object {
    fun currentLocalDateKey(): String {
      val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
      return formatter.format(Calendar.getInstance().time)
    }
  }
}
