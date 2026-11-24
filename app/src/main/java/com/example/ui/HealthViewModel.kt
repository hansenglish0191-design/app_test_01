package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DailyHealthRepository
import com.example.data.DailyHealthState
import com.example.data.MedicationSlot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class HealthUiState(
  val dateTitle: String = "",
  val dayOfWeekTitle: String = "",
  val healthState: DailyHealthState = DailyHealthState(dateKey = ""),
)

class HealthViewModel(
  private val repository: DailyHealthRepository,
  autoWatchMidnight: Boolean = true,
) : ViewModel() {

  private val currentDateKey = MutableStateFlow(repository.todayDateKey())

  val uiState: StateFlow<HealthUiState> =
    combine(repository.dailyState, currentDateKey) { state, activeDateKey ->
        val effectiveState =
          if (state.dateKey == activeDateKey) state else DailyHealthState(dateKey = activeDateKey)
        val (dateLine, dayLine) = formatKoreanDate(activeDateKey)
        HealthUiState(
          dateTitle = dateLine,
          dayOfWeekTitle = dayLine,
          healthState = effectiveState,
        )
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue =
          formatKoreanDate(currentDateKey.value).let { (dateLine, dayLine) ->
            HealthUiState(
              dateTitle = dateLine,
              dayOfWeekTitle = dayLine,
              healthState = DailyHealthState(dateKey = currentDateKey.value),
            )
          },
      )

  init {
    viewModelScope.launch { repository.resetIfDateChanged() }
    if (autoWatchMidnight) {
      startMidnightWatcher()
    }
  }

  private fun startMidnightWatcher() {
    viewModelScope.launch {
      while (isActive) {
        val waitMillis = millisUntilNextMidnight()
        delay(waitMillis)
        refreshDateIfNeeded()
      }
    }
  }

  fun refreshDateIfNeeded() {
    val today = repository.todayDateKey()
    if (currentDateKey.value != today) {
      currentDateKey.value = today
    }
    viewModelScope.launch { repository.resetIfDateChanged() }
  }

  fun onMedicationClick(slot: MedicationSlot) {
    refreshDateIfNeeded()
    viewModelScope.launch { repository.toggleMedication(slot) }
  }

  fun onAddWaterClick() {
    refreshDateIfNeeded()
    viewModelScope.launch { repository.addWaterCup() }
  }

  fun onRemoveWaterClick() {
    refreshDateIfNeeded()
    viewModelScope.launch { repository.removeWaterCup() }
  }

  private fun formatKoreanDate(dateKey: String): Pair<String, String> {
    val parsedDate =
      runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateKey) }.getOrNull()
        ?: Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.KOREAN)
    val dayFormat = SimpleDateFormat("EEEE", Locale.KOREAN)
    return dateFormat.format(parsedDate) to dayFormat.format(parsedDate)
  }

  companion object {
    fun millisUntilNextMidnight(): Long {
      val now = Calendar.getInstance()
      val nextMidnight =
        (now.clone() as Calendar).apply {
          add(Calendar.DAY_OF_YEAR, 1)
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 100)
        }
      return (nextMidnight.timeInMillis - now.timeInMillis).coerceAtLeast(1_000L)
    }

    fun factory(context: Context): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return HealthViewModel(DailyHealthRepository(context.applicationContext)) as T
        }
      }
  }
}
